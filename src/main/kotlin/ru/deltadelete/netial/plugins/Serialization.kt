package ru.deltadelete.netial.plugins

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.datetime.Instant


fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerKotlinModule()
            registerModule(
                SimpleModule()
                    .addSerializer(Instant::class.java, InstantSerializer())
                    .addDeserializer(Instant::class.java, InstantDeserializer())
            )
            findAndRegisterModules()
        }
    }
}

class InstantSerializer : JsonSerializer<Instant>() {
    override fun serialize(value: Instant?, jsonGenerator: JsonGenerator?, serializerProvider: SerializerProvider?) {
        jsonGenerator?.let { jGen ->
            value?.let { instant ->
                jGen.writeNumber(instant.epochSeconds)
            } ?: jGen.writeNull()
        }
    }
}

class InstantDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(parser: JsonParser?, p1: DeserializationContext?): Instant {
        parser?.readValueAs(Long::class.java)?.let {
            return Instant.fromEpochSeconds(it)
        }
        return Instant.DISTANT_PAST
    }
}