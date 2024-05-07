package ru.deltadelete.netial.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.deltadelete.netial.database.dao.User

fun PipelineContext<Unit, ApplicationCall>.principalUser() =
    call.authentication.principal<JWTPrincipal>()?.subject?.toLong()?.let {
        return@let User.findById(it)
    }

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }