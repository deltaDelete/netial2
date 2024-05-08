package ru.deltadelete.netial.routes.users

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import ru.deltadelete.netial.database.dao.UserService
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.dto.UserRegister


fun Application.configureUsers() = routing {
    val userService = UserService()
    // Create user
    post("/users") {
        createUser(userService)
    }

    // Register user
    post("/register") {
        createUser(userService)
    }

    // Get user info
    get("/users/{id}") {
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

    // Get list of users
    get("/users") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1L
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val users = userService.readAll(page, pageSize)
        call.respond(HttpStatusCode.OK, users)
    }

    authenticate("auth-jwt", strategy = AuthenticationStrategy.FirstSuccessful) {

        // Update user
        put("/users/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val authenticatedUserId = call.authentication.principal<JWTPrincipal>()?.subject?.toLong()

            if (authenticatedUserId != id) {
                call.respond(HttpStatusCode.Forbidden)
                return@put
            }

            val user = call.receive<UserDto>()
            userService.update(id, user)
            call.respond(HttpStatusCode.OK)
        }

        // Delete user
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val authenticatedUserId = call.authentication.principal<JWTPrincipal>()?.subject?.toLong()

            if (authenticatedUserId != id) {
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
}
