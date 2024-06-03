package ru.deltadelete.netial.routes.roles

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
import ru.deltadelete.netial.database.dao.Role
import ru.deltadelete.netial.database.dto.RoleDto
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.database.schemas.Roles
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.missingPermission
import ru.deltadelete.netial.utils.principalUser

fun Application.configureRoles() = routing {
    // GET: Get all roles
    get("/api/roles") {
        val page = call.request.queryParameters["page"]?.toLong() ?: 1
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val offset = (page - 1) * pageSize

        val roles = dbQuery {
            Role.find {
                (Roles.deletionDate eq null) and (Roles.isDeleted eq false)
            }
                .orderBy(Roles.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { RoleDto.from(it) }
        }

        call.respond(roles)
    }

    get("/api/roles/pages") {
        val pageSize = call.request.queryParameters["pageSize"]?.toInt() ?: 10
        val total = dbQuery {
            Role.find { (Roles.deletionDate eq null) and (Roles.isDeleted eq false) }
                .count()
        }
        val pages = (total + pageSize - 1) / pageSize
        call.respond(HttpStatusCode.OK, pages)
    }

    // GET: Get list of users with role
    get("/api/roles/{id}/users") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
        val users = dbQuery {
            Role.findById(id)?.users?.map {
                UserDto.from(it)
            }.orEmpty()
        }
        call.respond(HttpStatusCode.OK, users)
    }

    // GET: Get role by id
    get("/api/roles/{id}") {
        val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")

        val role = dbQuery {
            Role.findById(id)?.let {
                RoleDto.from(it)
            }
        }

        if (role == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        call.respond(HttpStatusCode.OK, role)
    }

    authenticate("auth-jwt") {
        // POST: Create new role
        post("/api/roles") {
            val user = principalUser()

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@post
            }

            val role = call.receive<RoleRequest>()

            user.missingPermission(Permission.CREATE_ROLE) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to create this role")
                return@post
            }

            val new = dbQuery {
                Role.new {
                    name = role.name
                    permissions = Permission.fromBitMask(role.permissions)
                }
            }

            call.respond(HttpStatusCode.Created, RoleDto.from(new))
        }

        // PUT: Update role text
        put("/api/roles/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@put
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val newRole = call.receive<RoleRequest>()

            val role = dbQuery { Role.findById(id) }
            if (role == null) {
                call.respond(HttpStatusCode.NotFound)
                return@put
            }

            user.missingPermission(Permission.MODIFY_ROLE) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to modify this role")
                return@put
            }

            transaction {
                role.name = newRole.name
                role.description = newRole.description
                role.permissions = Permission.fromBitMask(newRole.permissions)
            }

            call.respond(HttpStatusCode.OK, RoleDto.from(role))
        }

        // DELETE: Delete role
        delete("/api/roles/{id}") {
            val user = principalUser()
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid user")
                return@delete
            }

            val id = call.parameters["id"]?.toLong() ?: throw IllegalArgumentException("Invalid ID")
            val role = dbQuery { Role.findById(id) }
            if (role == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            user.missingPermission(Permission.REMOVE_ROLE) {
                call.respond(HttpStatusCode.Forbidden, "You don't have permission to delete this role")
                return@delete
            }

            transaction {
                role.isDeleted = true
                role.deletionDate = Clock.System.now()
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

