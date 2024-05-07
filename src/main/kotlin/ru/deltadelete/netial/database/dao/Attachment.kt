package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Attachments
import ru.deltadelete.netial.database.schemas.Users

/**
 * @see [Attachments]
 */
class Attachment(id: EntityID<Long>) : LongEntity(id) {

    companion object : LongEntityClass<Attachment>(Attachments)

    var name by Attachments.name
    var mimeType by Attachments.mimeType
    var hash by Attachments.hash
    var size by Attachments.size
    var user by User referencedOn Attachments.user

    var isDeleted by Users.isDeleted
    var creationDate by Users.creationDate
    var deletionDate by Users.deletionDate
}

