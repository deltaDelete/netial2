package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object CommentsLikes : Table("comments_likes") {
    val comment = reference("comment_id", Comments)
    val user = reference("user_id", Users)
    override val primaryKey = PrimaryKey(comment, user, name = "pk_comments_likes")
}