package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.database.schemas.Roles
import ru.deltadelete.netial.database.schemas.UsersRoles
import java.util.*

class Role(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Role>(Roles)

    var name by Roles.name
    var description by Roles.description
    var permissions: EnumSet<Permission> by Roles.permissions.transform(
        { permissions -> permissions.sumOf { it.value } },
        { Permission.fromBitMask(it) }
    )

    var isDeleted by Roles.isDeleted
    var creationDate by Roles.creationDate
    var deletionDate by Roles.deletionDate

    // TODO Check if it works
    val users by User via UsersRoles
}