package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object PostsLikes : Table("posts_likes") {
    val post = reference("post_id", Posts)
    val user = reference("user_id", Users)
    override val primaryKey = PrimaryKey(post, user, name = "pk_posts_likes")
}