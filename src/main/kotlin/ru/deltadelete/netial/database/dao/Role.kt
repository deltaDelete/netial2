package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Permission
import ru.deltadelete.netial.database.schemas.Roles
import ru.deltadelete.netial.database.schemas.UsersRoles
import java.util.*

class Role(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<Role>(Roles)

    var name by Roles.name
    var description by Roles.description
    var permissions: EnumSet<Permission> by Roles.permissions.transform(
        { permissions -> permissions.sumOf { it.value } },
        { Permission.fromBitMask(it) }
    )

    override var isDeleted by Roles.isDeleted
    override var creationDate by Roles.creationDate
    override var deletionDate by Roles.deletionDate

    val users by User via UsersRoles
}