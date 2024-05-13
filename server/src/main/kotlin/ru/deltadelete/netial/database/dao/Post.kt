package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Comments
import ru.deltadelete.netial.database.schemas.Posts
import ru.deltadelete.netial.database.schemas.PostsAttachments
import ru.deltadelete.netial.database.schemas.PostsLikes

class Post(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<Post>(Posts)

    var text by Posts.text
    var user by User referencedOn Posts.user
    var likes by Posts.likes
    var comments by Posts.comments
    var isArticle by Posts.isArticle

    override var isDeleted by Posts.isDeleted
    override var creationDate by Posts.creationDate
    override var deletionDate by Posts.deletionDate

    val commentList by Comment referrersOn Comments.post
    val attachments by Attachment via PostsAttachments
    val likeList by User via PostsLikes
}