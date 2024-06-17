package ru.deltadelete.netial.routes.attachments

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.Attachment
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.AttachmentDto
import ru.deltadelete.netial.database.schemas.Attachments
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.utils.*
import java.io.File

fun Application.configureAttachments() = routing {
    // GET: Get all attachments
    get("/api/attachments") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val offset = (page - 1) * pageSize

        val attachments = dbQuery {
            Attachment.find {
                (Attachments.deletionDate eq null) and (Attachments.isDeleted eq false)
            }
                .orderBy(Attachments.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { AttachmentDto.from(it) }
        }

        call.respond(attachments)
    }

    get("/api/attachments/pages") {
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val total = dbQuery {
            Attachment.find { (Attachments.deletionDate eq null) and (Attachments.isDeleted eq false) }
                .count()
        }
        val pages = (total + pageSize - 1) / pageSize
        call.respond(HttpStatusCode.OK, pages)
    }

    // GET: Get attachment by id
    get("/api/attachments/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

        val attachment = dbQuery {
            Attachment.findById(id)?.let {
                AttachmentDto.from(it)
            }
        }

        if (attachment == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, attachment)
    }

    get("/api/attachments/{id}/data") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
        val isDownload = call.request.queryParameters["download"] != null

        val attachment = dbQuery {
            Attachment.findById(id)?.let {
                AttachmentDto.from(it)
            }
        }

        if (attachment == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val file = attachmentFile(attachment.id.toString())
        if (!file.exists()) {
            call.respond(HttpStatusCode.NotFound, "File data not found. Probably it was deleted or not uploaded yet.")
        }
        call.response.header(
            HttpHeaders.ContentDisposition,
            "${if (isDownload) "attachment" else "inline"}; filename=\"${attachment.name}\""
        )
        call.response.header(HttpHeaders.ContentType, attachment.mimeType)
        call.respondOutputStream {
            file.inputStream().use {
                it.copyTo(this)
            }
        }
    }

    authenticate("auth-jwt") {
        // POST: Create new attachment
        post("/api/attachments") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val attachment = call.receive<AttachmentRequest>()
            val isSelf = attachment.userId?.let {
               it == user.id.value
            } ?: true

            user.checkPermission(Permission.CREATE_ATTACHMENT, Permission.SELF_CREATE_ATTACHMENT, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create this attachment")
                return@post
            }

            val entityUser = if (isSelf) {
                user
            } else {
                // if isSelf is false then user cannot be null
                dbQuery { User.findById(attachment.userId!!) }
            }

            if (entityUser == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user id in body")
                return@post
            }

            val new = dbQuery {
                Attachment.new {
                    this.name = attachment.name
                    this.mimeType = attachment.mimeType
                    this.size = attachment.size
                    this.user = entityUser
                }.load(Attachment::user)
            }

            call.respond(HttpStatusCode.Created, AttachmentDto.from(new))
        }

        // POST: Upload attachment data
        post("/api/attachments/{id}/data") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

            val attachment = dbQuery { Attachment.findById(id)?.load(Attachment::user) }
            if (attachment == null) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            val isSelf = attachment.user.id == user.id

            user.checkPermission(Permission.MODIFY_ATTACHMENT, Permission.SELF_MODIFY_ATTACHMENT, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this attachment")
                return@post
            }

            val file = attachmentFile(attachment.id.toString())

            file.createNewFile()
            launch(Dispatchers.IO) {
                call.receiveStream().use { attachmentStream ->
                    file.outputStream().use { fileStream ->
                        attachmentStream.copyTo(fileStream)
                    }
                }
                val hash = file.sha256()
                dbQuery {
                    attachment.hash = hash
                }
            }
            call.respond(HttpStatusCode.OK)
        }

        // PUT: Update attachment
        put("/api/attachments/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val newAttachment = call.receive<AttachmentRequest>()

            val attachment = dbQuery { Attachment.findById(id)?.load(Attachment::user) }
            if (attachment == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            val isSelf =
                (attachment.user.id.value == user.id.value) && (newAttachment.userId?.let { it == user.id.value }
                    ?: true)

            user.checkPermission(Permission.MODIFY_ATTACHMENT, Permission.SELF_MODIFY_ATTACHMENT, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this attachment")
                return@put
            }

            val entityUser = if (isSelf) {
                user
            } else {
                // if isSelf is false then user cannot be null
                dbQuery { User.findById(newAttachment.userId!!) }
            }

            if (entityUser == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user id in body")
                return@put
            }

            transaction {
                attachment.name = newAttachment.name
                attachment.mimeType = newAttachment.mimeType
                attachment.size = newAttachment.size
                attachment.user = entityUser
            }

            call.respond(HttpStatusCode.OK, AttachmentDto.from(attachment))
        }

        // DELETE: Delete attachment
        delete("/api/attachments/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val attachment = dbQuery { Attachment.findById(id)?.load(Attachment::user) }
            if (attachment == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            val isSelf = attachment.user.id == user.id

            user.checkPermission(Permission.REMOVE_ATTACHMENT, Permission.SELF_REMOVE_ATTACHMENT, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this attachment")
                return@delete
            }

            transaction {
                attachment.isDeleted = true
                attachment.deletionDate = Clock.System.now()
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun attachmentFile(name: String): File {
    val dir = File(Config.storage.attachments)
    if (!dir.exists()) {
        dir.mkdir()
    }
    dir.mkdir()
    val file = File(dir, name)
    return file
}
