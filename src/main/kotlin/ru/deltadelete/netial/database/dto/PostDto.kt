package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Post

data class PostDto(
    val text: String,
    val user: UserDto,
    val likes: Int,
    val comments: Int,
    val isArticle: Boolean,
    val creationDate: Instant,
    val isDeleted: Boolean,
    val deletionDate: Instant? = null,
    val id: Long = 0L,
) {
    companion object {
        fun from(post: Post): PostDto {
            return PostDto(
                post.text,
                UserDto.from(post.user),
                post.likes,
                post.comments,
                post.isArticle,
                post.creationDate,
                post.isDeleted,
                post.deletionDate,
                post.id.value
            )
        }
    }
}
