package ru.deltadelete.netial.database.schemas

object Roles: DeletableLongIdTable("roles") {
    val name = varchar("name", length = 50)
    val description = varchar("description", length = 255)
}