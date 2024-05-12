package ru.deltadelete.netial.plugins

import io.ktor.server.application.*
import ru.deltadelete.netial.utils.JsonConfig

fun Application.loadConfig() {
    install(JsonConfig.Plugin)
}