package ru.deltadelete.netial.routes.posts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andIfNotNull
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.Post
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.PostDto
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.database.schemas.Posts
import ru.deltadelete.netial.utils.checkPermission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser

fun Application.configurePosts() = routing {
    // GET: Get all posts
    get("/posts") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val isArticle = call.request.queryParameters["isArticle"]?.toBoolean()
        val offset = (page - 1) * pageSize

        val posts = dbQuery {
            return@dbQuery Post.find {
                Posts.deletionDate.eq(null)
                    .and(Posts.isDeleted eq false)
                    .andIfNotNull { isArticle?.let { Posts.isArticle eq it } }
            }
                .orderBy(Posts.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { PostDto.from(it) }
        }

        call.respond(HttpStatusCode.OK, posts)
    }

    // GET: Get post by id
    get("/posts/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")

        val post = dbQuery {
            Post.findById(id)?.let {
                PostDto.from(it)
            }
        }

        if (post == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(post)
    }

    authenticate("auth-jwt") {
        // POST: Create post
        post("/posts") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val post = call.receive<PostRequest>()

            val isSelf = post.user?.let {
                it == user.id.value
            } ?: true

            checkPermission(user, Permission.CREATE_POST, Permission.SELF_CREATE_POST, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create this post")
                return@post
            }

            val postUser = if (isSelf) {
                user
            } else {
                // if isSelf is false then user cannot be null
                dbQuery { User.findById(post.user!!) }
            }

            if (postUser == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user id in body")
                return@post
            }

            val new = dbQuery {
                Post.new {
                    this.text = post.text
                    this.user = postUser
                    this.isArticle = post.isArticle
                }
            }

            call.respond(HttpStatusCode.Created, PostDto.from(new))
        }

        // PUT: Update post text
        put("/posts/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val text = call.receiveText()

            val post = dbQuery { Post.findById(id) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            checkPermission(user, Permission.MODIFY_POST, Permission.SELF_MODIFY_POST, user.id == post.user.id) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this post")
                return@put
            }

            transaction {
                post.text = text
            }

            call.respond(HttpStatusCode.OK, PostDto.from(post))
        }

        // DELETE: Delete post
        delete("/posts/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val post = dbQuery { Post.findById(id) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            checkPermission(user, Permission.REMOVE_POST, Permission.SELF_REMOVE_POST, user.id == post.user.id) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this post")
                return@delete
            }

            dbQuery {
                post.isDeleted = true
                post.deletionDate = Clock.System.now()
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
