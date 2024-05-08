package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Role
import ru.deltadelete.netial.database.schemas.Permission
import java.util.*

data class RoleDto(
    val id: Long,
    val name: String,
    val permissions: EnumSet<Permission> = EnumSet.noneOf(Permission::class.java)
) {
    companion object : MappableDto<Role, RoleDto> {
        override fun from(from: Role): RoleDto {
            return RoleDto(
                from.id.value,
                from.name,
                from.permissions
            )
        }

        fun new(role: RoleDto): Role {
            return Role.new {
                name = role.name
                permissions = role.permissions
            }
        }
    }
}

