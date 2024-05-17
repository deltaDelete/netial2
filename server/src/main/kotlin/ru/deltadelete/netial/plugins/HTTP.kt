package ru.deltadelete.netial.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    routing {
        openAPI(path = "api/openapi")
        swaggerUI(path = "api/swagger")
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowCredentials)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.ContentLength)
        allowHeader(HttpHeaders.Date)
        allowHeader(HttpHeaders.Vary)
        allowHeader(HttpHeaders.Server)
        allowHeader(HttpHeaders.AcceptEncoding)
        allowHeader(HttpHeaders.UserAgent)
        allowHeader(HttpHeaders.Referrer)
        allowHeader(HttpHeaders.CacheControl)
        allowHeader(HttpHeaders.Pragma)
        allowHeader(HttpHeaders.Host)
        allowHeader(HttpHeaders.Connection)
    }
    install(DefaultHeaders)
}
