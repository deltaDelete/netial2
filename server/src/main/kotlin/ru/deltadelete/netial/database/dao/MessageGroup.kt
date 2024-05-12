package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Groups
import ru.deltadelete.netial.database.schemas.MessageGroupUsers
import ru.deltadelete.netial.database.schemas.MessageGroups
import ru.deltadelete.netial.database.schemas.Messages

class MessageGroup(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<MessageGroup>(MessageGroups)

    var name by Groups.name
    var creator by User referencedOn MessageGroups.creator

    override var isDeleted by MessageGroups.isDeleted
    override var creationDate by MessageGroups.creationDate
    override var deletionDate by MessageGroups.deletionDate

    val messages by Message optionalReferrersOn Messages.groupTo
    val users by User via MessageGroupUsers
}