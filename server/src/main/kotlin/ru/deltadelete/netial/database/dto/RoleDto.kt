package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Role
import ru.deltadelete.netial.database.schemas.Permission

data class RoleDto(
    val name: String,
    val description: String = "",
    val permissions: Long = 0,
    val isDeleted: Boolean = false,
    val creationDate: Instant = Clock.System.now(),
    val deletionDate: Instant? = null,
    val isDefault: Boolean = false,
    val id: Long = 0L,
) {
    companion object : MappableDto<Role, RoleDto> {
        override fun from(from: Role): RoleDto {
            return RoleDto(
                from.name,
                from.description,
                from.permissions.fold(0) { acc: Long, permission: Permission -> acc or permission.value },
                from.isDeleted,
                from.creationDate,
                from.deletionDate,
                from.isDefault,
                from.id.value,
            )
        }
    }
}

