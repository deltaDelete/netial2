package ru.deltadelete.netial.database.schemas

object MessageGroups : DeletableLongIdTable("message_groups") {
    val name = varchar("name", 255)
    val creator = reference("creator_id", Users)
}