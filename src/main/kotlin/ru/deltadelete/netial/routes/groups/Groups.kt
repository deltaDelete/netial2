package ru.deltadelete.netial.routes.groups

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.dao.Group
import ru.deltadelete.netial.database.dto.GroupDto
import ru.deltadelete.netial.database.schemas.Groups
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.missingPermission
import ru.deltadelete.netial.utils.principalUser

fun Application.configureGroups() = routing {
    // GET: Get all groups
    get("/groups") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val offset = (page - 1) * pageSize

        val groups = dbQuery {
            return@dbQuery Group.find {
                Groups.deletionDate.eq(null)
                    .and(Groups.isDeleted eq false)
            }
                .orderBy(Groups.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { GroupDto.from(it) }
        }

        call.respond(HttpStatusCode.OK, groups)
    }

    // GET: Get group by id
    get("/groups/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")

        val group = dbQuery {
            Group.findById(id)?.let {
                GroupDto.from(it)
            }
        }

        if (group == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(group)
    }

    authenticate("auth-jwt") {
        // POST: Create group
        post("/groups") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            user.missingPermission(Permission.CREATE_GROUP) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create groups")
                return@post
            }

            val group = call.receive<GroupRequest>()

            val new = dbQuery {
                Group.new {
                    name = group.name
                    description = group.description
                    year = group.year
                }
            }

            call.respond(HttpStatusCode.Created, GroupDto.from(new))
        }

        // PUT: Update group
        put("/groups/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            user.missingPermission(Permission.MODIFY_GROUP) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify groups")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val newGroup = call.receive<GroupRequest>()

            val group = dbQuery { Group.findById(id) }
            if (group == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            transaction {
                group.name = newGroup.name
                group.description = newGroup.description
                group.year = newGroup.year
            }

            call.respond(HttpStatusCode.OK, GroupDto.from(group))
        }

        // DELETE: Delete group
        delete("/groups/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            user.missingPermission(Permission.REMOVE_GROUP) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete groups")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid id")
            val group = dbQuery { Group.findById(id) }
            if (group == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            dbQuery {
                group.isDeleted = true
                group.deletionDate = Clock.System.now()
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
