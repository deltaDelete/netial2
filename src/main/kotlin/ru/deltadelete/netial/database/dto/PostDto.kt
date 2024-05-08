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
    companion object : MappableDto<Post, PostDto> {
        override fun from(from: Post): PostDto {
            return PostDto(
                from.text,
                UserDto.from(from.user),
                from.likes,
                from.comments,
                from.isArticle,
                from.creationDate,
                from.isDeleted,
                from.deletionDate,
                from.id.value
            )
        }
    }
}
