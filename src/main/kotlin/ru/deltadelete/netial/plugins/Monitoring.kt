package ru.deltadelete.netial.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
        if (this@configureMonitoring.developmentMode) {
            format { call ->
                val status = call.response.status()
                val headers =
                    call.request.headers.entries().joinToString("\n\t") { "${it.key}: ${it.value.joinToString("\t")}" }
                val method = call.request.httpMethod.value
                "${status}: $method - ${call.request.path()}\n\t$headers"
            }
        }
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }
}
