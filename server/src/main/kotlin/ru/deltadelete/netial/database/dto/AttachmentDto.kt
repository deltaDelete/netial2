package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Attachment

data class AttachmentDto(
    val name: String,
    val mimeType: String,
    val hash: String,
    val size: Long = 0L,
    val userId: Long = 0L,
    val user: UserDto? = null,
    val isDeleted: Boolean = false,
    val creationDate: Instant = Clock.System.now(),
    val deletionDate: Instant? = null,
    val id: Long = 0L,
) {
    companion object : MappableDto<Attachment, AttachmentDto> {
        override fun from(from: Attachment): AttachmentDto {
            return AttachmentDto(
                from.name,
                from.mimeType,
                from.hash,
                from.size,
                from.user.id.value,
                UserDto.from(from.user),
                from.isDeleted,
                from.creationDate,
                from.deletionDate,
                from.id.value
            )
        }
    }
}