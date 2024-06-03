package ru.deltadelete.netial.routes.posts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.Comment
import ru.deltadelete.netial.database.dao.Post
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.CommentDto
import ru.deltadelete.netial.database.dto.PostDto
import ru.deltadelete.netial.database.schemas.Comments
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.database.schemas.Posts
import ru.deltadelete.netial.database.schemas.PostsLikes
import ru.deltadelete.netial.utils.checkPermission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser

fun Application.configurePosts() = routing {
    // GET: Get all posts
    get("/api/posts") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val isArticle = call.request.queryParameters["isArticle"].toBoolean()
        val sortOrder = when (call.request.queryParameters["sort"]) {
            "asc" -> SortOrder.ASC
            else -> SortOrder.DESC
        }
        val offset = (page - 1) * pageSize

        val posts = dbQuery {
            Post.find {
                (Posts.isDeleted eq false) and (Posts.isArticle eq isArticle)
            }
                .orderBy(Posts.creationDate to sortOrder)
                .with(Post::user)
                .limit(pageSize, offset)
                .map { PostDto.from(it) }
        }

        call.respond(HttpStatusCode.OK, posts)
    }

    get("/api/posts/pages") {
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val total = dbQuery {
            Post.find { (Posts.deletionDate eq null) and (Posts.isDeleted eq false) }
                .count()
        }
        val pages = (total + pageSize - 1) / pageSize
        call.respond(HttpStatusCode.OK, pages)
    }

    // GET: Get post by id
    get("/api/posts/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

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

    // GET: Get posts comments by id
    get("/api/posts/{id}/comments") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

        val missing = dbQuery {
            Post.findById(id) == null
        }

        if (missing) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        val comments = dbQuery {
            Comment.find {
                (Comments.post eq id) and (Comments.isDeleted eq false)
            }.map {
                CommentDto.from(it)
            }
        }

        call.respond(comments)
    }

    authenticate("auth-jwt") {
        // POST: Create post
        post("/api/posts") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val post = call.receive<PostRequest>()

            val isSelf = post.userId?.let {
                it == user.id.value
            } ?: true

            user.checkPermission(Permission.CREATE_POST, Permission.SELF_CREATE_POST, isSelf) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create this post")
                return@post
            }

            val postUser = if (isSelf) {
                user
            } else {
                // if isSelf is false then user cannot be null
                dbQuery { User.findById(post.userId!!) }
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

        get("/api/posts/{id}/likes") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@get
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val hasLike = dbQuery {
                Post.findById(id)?.load(Post::user)?.likeList?.any { it.id == user.id }
            }
            if (hasLike == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(HttpStatusCode.OK, hasLike)
        }

        post("/api/posts/{id}/likes") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val post = dbQuery { Post.findById(id)?.load(Post::user) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }

            val newLikes = dbQuery {
                PostsLikes.insert {
                    it[PostsLikes.user] = user.id
                    it[PostsLikes.post] = post.id
                }
                return@dbQuery Post.reload(post)?.likes
            }

            call.respond(HttpStatusCode.OK, LikesResponse(post.id.value, newLikes ?: 0))
        }

        delete("/api/posts/{id}/likes") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val post = dbQuery { Post.findById(id)?.load(Post::user) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            val deletedRows = dbQuery {
                PostsLikes.deleteWhere(limit = 1) {
                    PostsLikes.user.eq(user.id).and(PostsLikes.post.eq(post.id))
                }
            }

            if (deletedRows > 0) {
                call.respond(HttpStatusCode.OK)
                return@delete
            }
            call.respond(HttpStatusCode.NotFound);
        }

        // PUT: Update post text
        put("/api/posts/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val text = call.receiveText()

            val post = dbQuery { Post.findById(id)?.load(Post::user) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            user.checkPermission(Permission.MODIFY_POST, Permission.SELF_MODIFY_POST, user.id == post.user.id) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this post")
                return@put
            }

            transaction {
                post.text = text
            }

            call.respond(HttpStatusCode.OK, PostDto.from(post))
        }

        // DELETE: Delete post
        delete("/api/posts/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val post = dbQuery { Post.findById(id)?.load(Post::user) }
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            user.checkPermission(Permission.REMOVE_POST, Permission.SELF_REMOVE_POST, user.id == post.user.id) {
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
