package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object UsersGroups : Table("users_groups") {
    val user = reference("user_id", Users)
    val group = reference("group_id", Groups)
    override val primaryKey = PrimaryKey(user, group, name = "pk_users_groups")
}