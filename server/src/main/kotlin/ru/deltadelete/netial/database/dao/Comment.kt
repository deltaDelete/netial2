package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Comments

class Comment(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<Comment>(Comments)

    var text by Comments.text
    var user by User referencedOn Comments.user
    var post by Post referencedOn Comments.post
    var likes by Comments.likes

    override var isDeleted by Comments.isDeleted
    override var creationDate by Comments.creationDate
    override var deletionDate by Comments.deletionDate
}