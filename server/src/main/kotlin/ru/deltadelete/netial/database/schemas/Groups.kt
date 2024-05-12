package ru.deltadelete.netial.database.schemas

object Groups: DeletableLongIdTable("groups") {
    val name = varchar("name", length = 50)
    val description = varchar("description", length = 255)
    val year = integer("year")
}