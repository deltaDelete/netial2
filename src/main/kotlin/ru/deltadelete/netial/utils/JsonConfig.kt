package ru.deltadelete.netial.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.jetbrains.annotations.ApiStatus.Experimental
import java.io.File
import kotlin.reflect.KProperty

/**
 * Ktor Plugin that supplies external config from json file
 */
@Experimental
class JsonConfig(
    private val file: File,
    val mapper: ObjectMapper
) {

    var map: Map<String, Any> = emptyMap()

    fun load() {
        map = mapper.readValue<Map<String, Any>>(file)
    }

    inline operator fun <reified T> getValue(instance: Config, property: KProperty<*>): T {
        return mapper.readValue<T>(mapper.writeValueAsBytes(map[property.name]))
    }

    class Configuration {
        var file: File = File("config.json")
        var mapper: ObjectMapper = jacksonObjectMapper()
    }

    companion object {
        private val LOGGER = KtorSimpleLogger("ru.deltadelete.netial.utils.JsonConfig")

        lateinit var instance: JsonConfig

        val Plugin = createApplicationPlugin(
            name = "JsonConfig",
            createConfiguration = { ::Configuration }
        ) {
            val config = pluginConfig()
            instance = JsonConfig(config.file, config.mapper)
            LOGGER.info("Starting JsonConfig")
            instance.load()
        }
    }
}

object Config {
    val secret: String by JsonConfig.instance
    val database: DatabaseConfig by JsonConfig.instance
    val jwt: JWTConfig by JsonConfig.instance
    val email: EmailConfig by JsonConfig.instance
    val templates: TemplatesConfig by JsonConfig.instance
    val storage: StorageConfig by JsonConfig.instance

    class DatabaseConfig(
        val url: String,
        val driver: String,
        val user: String,
        val password: String,
    )

    class JWTConfig(
        val jwtAudience: String,
        val jwtDomain: String,
        val jwtRealm: String,
        val jwtSecret: String,
    )

    class EmailConfig(
        val host: String,
        val port: String,
        val login: String,
        val password: String,
    )

    class TemplatesConfig(
        val emailConfirmationTemplate: String
    )

    class StorageConfig(
        val attachments: String
    )
}