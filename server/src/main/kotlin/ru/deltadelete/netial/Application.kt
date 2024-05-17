package ru.deltadelete.netial

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.deltadelete.netial.plugins.*
import ru.deltadelete.netial.routes.configureRoutes

fun main(args: Array<String>) {
    val environment = commandLineEnvironment(args) {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
        module(Application::module)
    }
    embeddedServer(
        Netty,
        environment
    ).start(wait = true)
}

fun Application.module() {
    loadConfig()
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureDatabases()
    configureRouting()
    configureRoutes()
}
