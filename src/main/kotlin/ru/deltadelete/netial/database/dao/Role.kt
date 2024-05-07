package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Roles

class Role(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Role>(Roles)

    var name by Roles.name
    var description by Roles.description

    var isDeleted by Roles.isDeleted
    var creationDate by Roles.creationDate
    var deletionDate by Roles.deletionDate
}