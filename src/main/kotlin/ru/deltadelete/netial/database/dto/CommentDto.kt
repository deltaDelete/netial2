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
    companion object {
        fun from(comment: Comment): CommentDto {
            return CommentDto(
                comment.id.value,
                comment.text,
                UserDto.from(comment.user),
                comment.post,
                comment.likes
            )
        }
    }
}