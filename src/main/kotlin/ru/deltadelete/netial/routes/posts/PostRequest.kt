package ru.deltadelete.netial.routes.posts

data class PostRequest(
    val text: String,
    val isArticle: Boolean,
)