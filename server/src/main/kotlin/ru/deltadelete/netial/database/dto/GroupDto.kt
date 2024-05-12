package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Group

data class GroupDto(
    val name: String,
    val description: String,
    val year: Int,
    val id: Long = 0L,
) {
    companion object : MappableDto<Group, GroupDto> {
        override fun from(from: Group): GroupDto {
            return GroupDto(
                from.name,
                from.description,
                from.year,
                from.id.value
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