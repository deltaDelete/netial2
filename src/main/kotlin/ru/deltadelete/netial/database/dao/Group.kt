package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Groups

class Group(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Group>(Groups)

    var name by Groups.name
    var description by Groups.description
    var year by Groups.year

    var isDeleted by Groups.isDeleted
    var creationDate by Groups.creationDate
    var deletionDate by Groups.deletionDate
}