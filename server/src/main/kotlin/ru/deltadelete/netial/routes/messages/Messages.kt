package ru.deltadelete.netial.routes.messages

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import ru.deltadelete.netial.database.dao.Message
import ru.deltadelete.netial.database.dto.MessageDto
import ru.deltadelete.netial.database.schemas.Messages
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.routes.deleteById
import ru.deltadelete.netial.routes.getById
import ru.deltadelete.netial.utils.checkPermission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser

fun Application.configureMessages() = routing {
    authenticate("auth-jwt") {
        route("/api/messages") {
            get {
                val page = call.request.queryParameters["page"]?.toLong() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
                val userIdTo = call.request.queryParameters["userTo"]?.toLong()
                val groupIdTo = call.request.queryParameters["groupTo"]?.toLong()
                val offset = (page - 1) * pageSize

                if (userIdTo == null || groupIdTo == null) {
                    call.respond(HttpStatusCode.BadRequest, "Either userTo or groupTo query parameters must be specified")
                    return@get
                }

                val user = principalUser()
                if (user == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user")
                    return@get
                }

                val entities = dbQuery {
                    Message.find {
                        (Messages.deletionDate eq null)
                            .and(Messages.isDeleted eq false)
                            .and(
                                (Messages.user eq user.id).or(Messages.userTo eq user.id)
                            )
                            .and(
                                (Messages.userTo eq userIdTo).or(Messages.user eq userIdTo)
                            )
                            .and(Messages.groupTo eq groupIdTo)
                    }
                        .orderBy(Messages.creationDate to SortOrder.DESC)
                        .limit(pageSize, offset)
                        .map(MessageDto::from)
                }

                call.respond(HttpStatusCode.OK, entities)
            }

            getById(Message.Companion, MessageDto::from)
            deleteById(
                entityCompanion = Message.Companion,
                loader = {
                    load(Message::user)
                }
            ) { it, user ->
                val isSelf = it.user.id == user.id
                user.checkPermission(Permission.REMOVE_MESSAGE, Permission.SELF_REMOVE_MESSAGE, isSelf) {
                    return@deleteById false
                }
                return@deleteById true
            }

            put("/{id}") {
                val user = principalUser()
                if (user == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user")
                    return@put
                }

                val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
                val newText = call.receiveText()

                val entity = dbQuery { Message.findById(id)?.load(Message::user) }
                if (entity == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@put
                }

                val isSelf = entity.user.id.value == user.id.value

                user.checkPermission(Permission.MODIFY_MESSAGE, Permission.SELF_MODIFY_MESSAGE, isSelf) {
                    call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this message")
                    return@put
                }

                dbQuery {
                    entity.text = newText
                }

                call.respond(HttpStatusCode.OK, MessageDto.from(entity))
            }
        }
    }
}
