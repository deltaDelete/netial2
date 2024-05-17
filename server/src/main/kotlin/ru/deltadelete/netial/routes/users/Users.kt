package ru.deltadelete.netial.routes.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dao.UserService
import ru.deltadelete.netial.database.dto.RoleDto
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.dto.UserRegister
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.utils.checkPermission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser


fun Application.configureUsers() = routing {
    val userService = UserService()

    // TODO: CHANGE AND RESET PASSWORD
    // TODO: AVATARS

    // Create user
    post("/api/users") {
        createUser(userService)
    }

    // Register user
    post("/api/register") {
        createUser(userService)
    }

    // Get user info
    get("/api/users/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
        val user = userService.read(id)
        if (user != null) {
            call.respond(
                status = HttpStatusCode.OK,
                message = UserDto.from(user)
            )
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    // GET: Get list of roles for user
    get("/api/users/{id}/roles") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
        val roles = dbQuery {
            User.findById(id)?.roles?.map {
                RoleDto.from(it)
            }.orEmpty()
        }
        call.respond(HttpStatusCode.OK, roles)
    }

    // Get list of users
    get("/api/users") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1L
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val users = userService.readAll(page, pageSize)
        call.respond(HttpStatusCode.OK, users)
    }

    // GET: Confirm email
    get("/api/users/{id}/confirm") {
        val token = call.request.queryParameters["token"] ?: throw IllegalArgumentException("Invalid token")
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

        when (userService.confirmEmail(id, token)) {
            UserService.EmailConfirmResult.OK -> {
                call.respondText("Confirmation successful", status = HttpStatusCode.OK)
            }
            UserService.EmailConfirmResult.USER_NOT_FOUND -> {
                call.respondText("User with ID=$id not found", status = HttpStatusCode.NotFound)
            }
            UserService.EmailConfirmResult.INVALID_CODE -> {
                call.respondText("Invalid confirmation code", status = HttpStatusCode.BadRequest)
            }
        }
    }

    authenticate("auth-jwt", strategy = AuthenticationStrategy.FirstSuccessful) {

        // Update user
        put("/api/users/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }

            val isSelf = id != user.id.value
            user.checkPermission(Permission.MODIFY_USER, Permission.SELF_MODIFY_USER, isSelf) {
                call.respond(HttpStatusCode.Forbidden)
                return@put
            }

            val newUser = call.receive<UserDto>()
            userService.update(id, newUser)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/api/users/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val isSelf = id == user.id.value
            user.checkPermission(Permission.REMOVE_USER, Permission.SELF_REMOVE_USER, isSelf) {
                call.respond(HttpStatusCode.Forbidden)
                return@delete
            }

            userService.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.createUser(
    userService: UserService,
) {
    val user = call.receive<UserRegister>()
    val id = userService.create(user as UserDto, user.password)
    call.respond(HttpStatusCode.Created, id)
    userService.sendConfirmationEmail(id)
}
