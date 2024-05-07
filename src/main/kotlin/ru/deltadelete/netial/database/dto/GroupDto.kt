package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Group

data class GroupDto(
    val id: Long,
    val name: String,
    val description: String
) {
    companion object {
        fun from(group: Group): GroupDto {
            return GroupDto(
                group.id.value,
                group.name,
                group.description
            )
        }

        fun new(group: GroupDto): Group {
            return Group.new {
                name = group.name
                description = group.description
            }
        }
    }
}