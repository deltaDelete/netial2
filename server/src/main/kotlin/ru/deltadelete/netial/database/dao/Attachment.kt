package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Attachments

/**
 * @see [Attachments]
 */
class Attachment(id: EntityID<Long>) : LongEntity(id), DeletableEntity {

    companion object : LongEntityClass<Attachment>(Attachments)

    var name by Attachments.name
    var mimeType by Attachments.mimeType
    var hash by Attachments.hash
    var size by Attachments.size
    var user by User referencedOn Attachments.user

    override var isDeleted by Attachments.isDeleted
    override var creationDate by Attachments.creationDate
    override var deletionDate by Attachments.deletionDate
}

