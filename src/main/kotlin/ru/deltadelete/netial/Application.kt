package ru.deltadelete.netial

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.deltadelete.netial.plugins.*
import ru.deltadelete.netial.routes.configureRoutes
import java.io.File
import java.security.KeyStore

fun main() {
    val keyStoreFile = File("/home/delta/.keys/netial-keystore.jks")
    val keyStore = KeyStore.getInstance(keyStoreFile, "Povar1315".toCharArray())
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
            host = "0.0.0.0"
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "netial",
            keyStorePassword = { "Povar1315".toCharArray() },
            privateKeyPassword = { "Povar1315".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
    embeddedServer(
        Netty,
        environment
    ).start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
    configureSockets()
    configureDatabases()
    configureRouting()
    configureRoutes()
}
