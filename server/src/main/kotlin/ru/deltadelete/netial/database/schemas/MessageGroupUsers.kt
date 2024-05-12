package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object MessageGroupUsers : Table("message_group_users") {
    val user = reference("user_id", Users)
    val group = reference("message_group_id", MessageGroups)
    override val primaryKey = PrimaryKey(user, group, name = "pk_message_groups_users")
}