package ru.deltadelete.netial.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*
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
        allowHeader(HttpHeaders.AcceptLanguage)
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
        allowHeader(HttpHeaders.Forwarded)
        allowHeader(HttpHeaders.XForwardedFor)
        allowHeader(HttpHeaders.XForwardedHost)
        allowHeader(HttpHeaders.XForwardedProto)
        allowHeadersPrefixed("Sec-Websocket")
        allowHeader("Cdn-Loop")
        allowHeadersPrefixed("Cf-")
        allowHeadersPrefixed("X-Vercel-")
    }
    install(DefaultHeaders)

    routing {
        install(CachingHeaders) {
            options { call, content ->
                return@options when (content.contentType?.withoutParameters()) {
                    ContentType.Text.Plain -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
                    ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 3600))
                    ContentType.Application.Json -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 300))
                    else -> null
                }
            }
        }
    }
}
