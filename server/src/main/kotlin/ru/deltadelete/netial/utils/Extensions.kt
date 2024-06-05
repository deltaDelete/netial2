package ru.deltadelete.netial.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.websocket.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.mindrot.jbcrypt.BCrypt
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.plugins.InstantDeserializer
import ru.deltadelete.netial.plugins.InstantSerializer
import java.io.File
import java.security.MessageDigest

suspend fun PipelineContext<Unit, ApplicationCall>.principalUser() = dbQuery {
    call.authentication.principal<JWTPrincipal>()?.subject?.toLong()?.let {
        return@let User.findById(it)
    }
}

suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

/**
 * Проверяет наличие разрешений
 * @param bypassPermission Разрешение при наличии которого не требуется наличие
 * @param weakPermission Основное разрешение
 * @param isWeak false - если разрешение bypass необходимо, true - если нет
 * @param block код выполняемый при отсутствии каких-либо разрешений
 */
suspend inline fun User.checkPermission(
    bypassPermission: Permission,
    weakPermission: Permission,
    isWeak: Boolean,
    block: () -> Unit,
): Boolean {
    val hasBypassPermission = dbQuery {
        roles.any {
            it.permissions.contains(bypassPermission)
        }
    }
    val hasWeakPermission = dbQuery {
        roles.any {
            it.permissions.contains(weakPermission)
        }
    }

    if (!hasBypassPermission && !(hasWeakPermission && isWeak)) {
        block()
        return false
    }
    return true
}

suspend inline fun User.hasPermission(
    permission: Permission,
    block: () -> Unit,
): Boolean {
    val hasPermission = dbQuery {
        roles.any {
            it.permissions.contains(permission)
        }
    }
    if (hasPermission) {
        block()
    }
    return hasPermission
}

suspend inline fun User.missingPermission(
    permission: Permission,
    block: () -> Unit,
): Boolean {
    val hasPermission = dbQuery {
        roles.any {
            it.permissions.contains(permission)
        }
    }
    if (!hasPermission) {
        block()
    }
    return !hasPermission
}

fun User.generateConfirmationCode(secret: String): String {
    val map = mapOf("id" to id.value, "email" to email, "secret" to secret).toJson()
    val salt = BCrypt.gensalt()
    val code = BCrypt.hashpw(map, salt)
    return code
}

fun User.checkConfirmationCode(code: String, secret: String): Boolean {
    val map = mapOf("id" to id.value, "email" to email, "secret" to secret).toJson()
    return BCrypt.checkpw(map, code)
}

fun File.sha256(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    inputStream().use { input ->
        val buffer = ByteArray(8192)
        var bytesRead = input.read(buffer)
        while (bytesRead != -1) {
            messageDigest.update(buffer, 0, bytesRead)
            bytesRead = input.read(buffer)
        }
    }
    val bytes = messageDigest.digest()
    return bytes.joinToString("") { "%02x".format(it) }
}

fun Map<String, String>.formatTemplate(template: String): String {
    return entries.fold(template) { acc, entry ->
        acc.replace("\$${entry.key}\$", entry.value)
    }
}

fun Map<String, Any>.toJson(): String {
    val mapper = newJsonMapper()
    return mapper.writeValueAsString(this)
}

fun newJsonMapper(): ObjectMapper = jacksonObjectMapper().configureJackson()

fun ObjectMapper.configureJackson(): ObjectMapper = enable(SerializationFeature.INDENT_OUTPUT)
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .registerModule(
        SimpleModule()
            .addSerializer(Instant::class.java, InstantSerializer())
            .addDeserializer(Instant::class.java, InstantDeserializer())
    )
    .registerKotlinModule()
    .findAndRegisterModules()

val DefaultWebSocketServerSession.jsonMapper: ObjectMapper by lazy() { newJsonMapper() }

fun DefaultWebSocketServerSession.JsonFrame(data: Any) = Frame.Text(jsonMapper.writeValueAsString(data))