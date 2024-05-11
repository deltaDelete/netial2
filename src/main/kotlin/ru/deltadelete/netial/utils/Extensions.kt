package ru.deltadelete.netial.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.schemas.Permission
import java.io.File
import java.security.MessageDigest

suspend fun PipelineContext<Unit, ApplicationCall>.principalUser() = dbQuery {
    call.authentication.principal<JWTPrincipal>()?.subject?.toLong()?.let {
        return@let User.findById(it)
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
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

inline fun User.hasPermission(
    permission: Permission,
    block: () -> Unit,
): Boolean {
    val hasPermission = roles.any {
        it.permissions.contains(permission)
    }
    if (hasPermission) {
        block()
    }
    return hasPermission
}

inline fun User.missingPermission(
    permission: Permission,
    block: () -> Unit,
): Boolean {
    val hasPermission = roles.any {
        it.permissions.contains(permission)
    }
    if (!hasPermission) {
        block()
    }
    return !hasPermission
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