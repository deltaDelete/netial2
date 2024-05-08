package ru.deltadelete.netial.database.dto

import ru.deltadelete.netial.database.dao.Comment
import ru.deltadelete.netial.database.dao.Post

data class CommentDto(
    val id: Long,
    val text: String,
    val user: UserDto,
    val post: Post,
    val likes: Int
) {
    companion object : MappableDto<Comment, CommentDto> {
        override fun from(from: Comment): CommentDto {
            return CommentDto(
                from.id.value,
                from.text,
                UserDto.from(from.user),
                from.post,
                from.likes
            )
        }
    }
}