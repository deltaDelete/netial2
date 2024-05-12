package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.MessageGroup

data class MessageGroupDto(
    val name: String,
    val creatorId: Long,
    val isDeleted: Boolean = false,
    val deletionDate: Instant? = null,
    val creationDate: Instant = Clock.System.now(),
    val id: Long = 0L,
) {
    companion object : MappableDto<MessageGroup, MessageGroupDto> {
        override fun from(from: MessageGroup): MessageGroupDto {
            return MessageGroupDto(
                from.name,
                from.creator.id.value,
                from.isDeleted,
                from.deletionDate,
                from.creationDate,
                from.id.value
            )
        }
    }
}