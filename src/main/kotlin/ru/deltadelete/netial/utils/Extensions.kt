package ru.deltadelete.netial.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.schemas.Permission

fun PipelineContext<Unit, ApplicationCall>.principalUser() =
    call.authentication.principal<JWTPrincipal>()?.subject?.toLong()?.let {
        return@let User.findById(it)
    }

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }

inline fun checkPermission(
    user: User,
    bypassPermission: Permission,
    weakPermission: Permission,
    isBypassRequired: Boolean,
    block: () -> Unit,
) {
    val hasBypassPermission = user.roles.any {
        it.permissions.contains(bypassPermission)
    }
    val hasWeakPermission = user.roles.any {
        it.permissions.contains(weakPermission)
    }

    if (!hasBypassPermission && !(hasWeakPermission && isBypassRequired)) {
        block()
    }
}

inline fun User.hasPermission(
    permission: Permission,
    block: () -> Unit
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
    block: () -> Unit
): Boolean {
    val hasPermission = roles.any {
        it.permissions.contains(permission)
    }
    if (!hasPermission) {
        block()
    }
    return !hasPermission
}