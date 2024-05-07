package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Role

data class RoleDto(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(role: Role): RoleDto {
            return RoleDto(
                role.id.value,
                role.name
            )
        }

        fun new(role: RoleDto): Role {
            return Role.new {
                name = role.name
            }
        }
    }
}

