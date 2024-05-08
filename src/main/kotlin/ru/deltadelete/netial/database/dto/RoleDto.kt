package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Role

data class RoleDto(
    val id: Long,
    val name: String
) {
    companion object : MappableDto<Role, RoleDto> {
        override fun from(from: Role): RoleDto {
            return RoleDto(
                from.id.value,
                from.name
            )
        }

        fun new(role: RoleDto): Role {
            return Role.new {
                name = role.name
            }
        }
    }
}

