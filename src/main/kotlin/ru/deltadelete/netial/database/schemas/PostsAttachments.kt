package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object PostsAttachments : Table("posts_attachments") {
    val post = reference("post_id", Posts)
    val attachment = reference("attachment_id", Attachments)
    override val primaryKey = PrimaryKey(post, attachment, name = "pk_posts_attachments")
}