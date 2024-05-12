package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Role
import ru.deltadelete.netial.database.schemas.Permission
import java.util.*

data class RoleDto(
    val name: String,
    val description: String = "",
    val permissions: EnumSet<Permission> = EnumSet.noneOf(Permission::class.java),
    val isDeleted: Boolean = false,
    val creationDate: Instant = Clock.System.now(),
    val deletionDate: Instant? = null,
    val id: Long = 0L,
) {
    companion object : MappableDto<Role, RoleDto> {
        override fun from(from: Role): RoleDto {
            return RoleDto(
                from.name,
                from.description,
                from.permissions,
                from.isDeleted,
                from.creationDate,
                from.deletionDate,
                from.id.value,
            )
        }
    }
}

