package ru.deltadelete.netial.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.deltadelete.netial.types.Error
import ru.deltadelete.netial.utils.Config

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (call.request.headers["Accept"] == "application/json") {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    Error.ExceptionError(
                        exception = cause,
                        message = cause.message ?: "Unknown error",
                        statusCode = HttpStatusCode.InternalServerError
                    )
                )
            } else {
                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
            }
        }

        status(HttpStatusCode.Unauthorized) { call, code ->
            call.respond(
                code,
                Error.UserError("Unauthorized", code)
            )
        }
    }
    routing {
        // Endpoint for testing json representation of errors
        get("/api/error") {
            throw Exception("Error!")
        }

        singlePageApplication {
            useResources = false
            filesPath = Config.storage.wwwRoot
            defaultPage = "index.html"
        }
    }
}
