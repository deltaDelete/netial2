package ru.deltadelete.netial

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.plugins.*
import ru.deltadelete.netial.routes.users.configureUsers
import java.util.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testDatabase() = testApplication {
        application {
            configureDatabases()
            assertNotNull(TransactionManager.defaultDatabase)
        }
    }

    @Test
    fun testUsersGet() = testApplication {
        val mapper = mapper()
        application {
            configureDatabases()
            configureSecurity()
            configureSerialization()
            configureUsers()
        }
        client.get("/users") {
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = bodyAsText()
            assertNotEquals("", body, "Body is empty")

            val users = mapper.readValue<List<UserDto>>(body)
            assertTrue {
                users.isNotEmpty()
            }
        }
    }

    @Test
    fun testUsersSchemaRoles() = testApplication {
        application {
            configureDatabases()
            transaction {
                assertTrue {
                    val user = User.all().firstOrNull()
                    val roles = user?.roles
                    println(roles?.first()?.permissions)
                    return@assertTrue roles?.any() ?: false
                }
            }
        }
    }

    @Test
    fun testInstantSerializer() {
        val mapper = mapper()
        val input = Instant.fromEpochSeconds(1097712000)
        val expected = "1097712000"

        val output = mapper.writeValueAsString(input)

        assertEquals(expected, output)
    }

    @Test
    fun testInstantDeserializer() {
        val mapper = mapper()
        val input = "1097712000"
        val expected = Instant.fromEpochSeconds(1097712000)

        val output = mapper.readValue<Instant>(input)

        assertEquals(expected.epochSeconds, output.epochSeconds)
    }

    @Test
    fun testEnumSetSerializer() {
        val mapper = jacksonObjectMapper()
        val input = EnumSet.of(Permission.CREATE_ROLE, Permission.MODIFY_ROLE)
        val expected = """
            ["CREATE_ROLE","MODIFY_ROLE"]
        """.trimIndent()

        val output = mapper.writeValueAsString(input)

        assertEquals(expected, output)
    }

    @Test
    fun testEnumSetDeserializer() {
        val mapper = jacksonObjectMapper()
        val input = """
            ["CREATE_ROLE","MODIFY_ROLE"]
        """.trimIndent()
        val expected = EnumSet.of(Permission.CREATE_ROLE, Permission.MODIFY_ROLE)

        val output = mapper.readValue<EnumSet<Permission>>(input)

        assertEquals(expected, output)
    }
}

fun Application.configureTestDatabases() {
    // in memory h2
    val database = Database.connect(
        url = "jdbc:h2:mem:test",
        driver = "org.h2.Driver",
        user = "sa",
        password = ""
    )
}

fun mapper(): ObjectMapper = jacksonObjectMapper()
    .registerModules(
        SimpleModule()
            .addSerializer(Instant::class.java, InstantSerializer())
            .addDeserializer(Instant::class.java, InstantDeserializer())
    )