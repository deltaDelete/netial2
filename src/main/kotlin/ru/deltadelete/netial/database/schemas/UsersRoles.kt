package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object UsersRoles : Table("users_roles") {
    val user = reference("user_id", Users)
    val role = reference("role_id", Roles)
    override val primaryKey = PrimaryKey(user, role, name = "pk_users_roles")
}