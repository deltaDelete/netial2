package ru.deltadelete.netial.routes.comments

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.Comment
import ru.deltadelete.netial.database.dao.Post
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.CommentDto
import ru.deltadelete.netial.database.schemas.Comments
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.utils.checkPermission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser

fun Application.configureComments() = routing {
    // GET: Get all comments
    get("/comments") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val offset = (page - 1) * pageSize

        val comments = dbQuery {
            Comment.find {
                (Comments.deletionDate eq null) and (Comments.isDeleted eq false)
            }
                .orderBy(Comments.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { CommentDto.from(it) }
        }

        call.respond(comments)
    }

    // GET: Get comment by id
    get("/comments/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")

        val comment = dbQuery {
            Comment.findById(id)?.let {
                CommentDto.from(it)
            }
        }

        if (comment == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, comment)
    }

    authenticate("auth-jwt") {
        // POST: Create new comment
        post("/comments") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val comment = call.receive<CommentRequest>()

            val post = dbQuery { Post.findById(comment.postId) }

            if (post == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val isSelf = comment.user?.let {
                it == user.id.value
            } ?: true

            user.checkPermission(Permission.CREATE_COMMENT, Permission.SELF_CREATE_COMMENT, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create this comment")
                return@post
            }

            val commentUser = if (isSelf) {
                user
            } else {
                // if isSelf is false then user cannot be null
                dbQuery { User.findById(comment.user!!) }
            }

            if (commentUser == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user id in body")
                return@post
            }

            val new = dbQuery {
                Comment.new {
                    this.text = comment.text
                    this.post = post
                    this.user = commentUser
                }
            }

            call.respond(HttpStatusCode.Created, CommentDto.from(new))
        }

        // PUT: Update comment text
        put("/comments/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val text = call.receiveText()

            val comment = dbQuery { Comment.findById(id) }
            if (comment == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            user.checkPermission(Permission.MODIFY_COMMENT, Permission.SELF_MODIFY_COMMENT, user.id == comment.user.id) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this comment")
                return@put
            }

            transaction {
                comment.text = text
            }

            call.respond(HttpStatusCode.OK, CommentDto.from(comment))
        }

        // DELETE: Delete comment
        delete("/comments/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val comment = dbQuery { Comment.findById(id)?.load(Comment::user) }
            if (comment == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            user.checkPermission(Permission.REMOVE_COMMENT, Permission.SELF_REMOVE_COMMENT, user.id == comment.user.id) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this comment")
                return@delete
            }

            transaction {
                comment.isDeleted = true
                comment.deletionDate = Clock.System.now()
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
