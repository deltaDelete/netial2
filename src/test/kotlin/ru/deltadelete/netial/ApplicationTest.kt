package ru.deltadelete.netial

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.plugins.InstantDeserializer
import ru.deltadelete.netial.plugins.InstantSerializer
import ru.deltadelete.netial.plugins.configureRouting
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testInstantSerializer() {
        val mapper = jacksonObjectMapper()
            .registerModules(
                SimpleModule()
                    .addSerializer(Instant::class.java, InstantSerializer())
                    .addDeserializer(Instant::class.java, InstantDeserializer())
            )
        val input = Instant.fromEpochSeconds(1097712000)
        val expected = "1097712000"

        val output = mapper.writeValueAsString(input)

        assertEquals(expected, output)
    }

    @Test
    fun testInstantDeserializer() {
        val mapper = jacksonObjectMapper()
            .registerModules(
                SimpleModule()
                    .addSerializer(Instant::class.java, InstantSerializer())
                    .addDeserializer(Instant::class.java, InstantDeserializer())
            )
        val input = "1097712000"
        val expected = Instant.fromEpochSeconds(1097712000)

        val output = mapper.readValue<Instant>(input)

        assertEquals(expected.epochSeconds, output.epochSeconds)
    }
}
