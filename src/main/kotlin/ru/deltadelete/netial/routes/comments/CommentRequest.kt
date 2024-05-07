package ru.deltadelete.netial.routes.comments

data class CommentRequest(
    val text: String,
    val postId: Long
)