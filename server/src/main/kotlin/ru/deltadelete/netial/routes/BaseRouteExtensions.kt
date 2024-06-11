package ru.deltadelete.netial.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.DeletableEntity
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.schemas.DeletableLongIdTable
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.principalUser

/**
 * Must be inside a subroute
 */
inline fun <T, reified DTO> Route.getById(
    entityCompanion: LongEntityClass<T>,
    crossinline dtoMapper: (it: T) -> DTO,
)
        where T : LongEntity,
              T : DeletableEntity {
    get("/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

        val entity = dbQuery {
            entityCompanion.findById(id)?.let(dtoMapper)
        }

        if (entity == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, entity)
    }
}

inline fun <T, reified DTO> Route.getAll(
    entityCompanion: LongEntityClass<T>,
    tableObject: DeletableLongIdTable,
    crossinline dtoMapper: (it: T) -> DTO,
)
        where T : LongEntity,
              T : DeletableEntity {
    get {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val offset = (page - 1) * pageSize

        val entities = dbQuery {
            entityCompanion.find {
                (tableObject.deletionDate eq null) and (tableObject.isDeleted eq false)
            }
                .orderBy(tableObject.creationDate to SortOrder.DESC)
                .limit(pageSize, offset)
                .map(dtoMapper)
        }

        call.respond(HttpStatusCode.OK, entities)
    }
}

/**
 * @param entityCompanion Data Access Object
 * @param loader Подгрузка полей используемый в permissionChecker здесь
 * @param permissionChecker must return false if permission check is failed
 */
inline fun <T> Route.deleteById(
    entityCompanion: LongEntityClass<T>,
    crossinline loader: T.() -> T,
    crossinline permissionChecker: suspend PipelineContext<*, ApplicationCall>.(it: T, user: User) -> Boolean
)
        where T : LongEntity,
              T : DeletableEntity {
    delete("/{id}") {
        val user = principalUser()
        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid user")
            return@delete
        }

        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
        val entity = dbQuery { entityCompanion.findById(id)?.loader() }
        if (entity == null) {
            call.respond(HttpStatusCode.NotFound)
            return@delete
        }

        if (!permissionChecker(entity, user)) {
            call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this entity")
            return@delete
        }

        transaction {
            entity.isDeleted = true
            entity.deletionDate = Clock.System.now()
        }
        call.respond(HttpStatusCode.NoContent)
    }
}