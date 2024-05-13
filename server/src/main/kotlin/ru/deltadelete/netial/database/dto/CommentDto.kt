package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Comment

data class CommentDto(
    val text: String,
    val user: UserDto,
    val post: PostDto,
    val likes: Int,
    val isDeleted: Boolean = false,
    val creationDate: Instant = Clock.System.now(),
    val deletionDate: Instant? = null,
    val id: Long = 0L,
) {
    companion object : MappableDto<Comment, CommentDto> {
        override fun from(from: Comment): CommentDto {
            return CommentDto(
                from.text,
                UserDto.from(from.user),
                PostDto.from(from.post),
                from.likes,
                from.isDeleted,
                from.creationDate,
                from.deletionDate,
                from.id.value,
            )
        }
    }
}