package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Groups
import ru.deltadelete.netial.database.schemas.UsersGroups

class Group(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<Group>(Groups)

    var name by Groups.name
    var description by Groups.description
    var year by Groups.year

    override var isDeleted by Groups.isDeleted
    override var creationDate by Groups.creationDate
    override var deletionDate by Groups.deletionDate

    val users by User via UsersGroups
}