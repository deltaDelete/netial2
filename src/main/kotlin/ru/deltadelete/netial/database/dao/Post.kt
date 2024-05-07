package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Posts
import ru.deltadelete.netial.database.schemas.Users

class Post(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Post>(Posts)

    var text by Posts.text
    var user by User referencedOn Posts.user
    var likes by Posts.likes
    var comments by Posts.comments
    var isArticle by Posts.isArticle

    var isDeleted by Users.isDeleted
    var creationDate by Users.creationDate
    var deletionDate by Users.deletionDate
}