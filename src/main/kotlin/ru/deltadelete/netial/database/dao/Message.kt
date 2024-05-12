package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Messages
import ru.deltadelete.netial.database.schemas.MessagesAttachments

/**
 * Сообщение
 * @property text Текст сообщения
 * @property user Пользователь, создавший сообщение
 * @property userTo Пользователь, получатель
 * @property groupTo Группа, получатель
 * @property replyTo Ответ на другое сообщение,
 * в случае если сообщение отсылается как ответ на сообщение,
 * находящееся в группе, replyTo и groupTo должны быть установлены
 * @property attachments Прикрепленные файлы
 */
class Message(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<Message>(Messages)

    var text by Messages.text
    var user by User referencedOn Messages.user
    var userTo by User optionalReferencedOn Messages.userTo
    var groupTo by MessageGroup optionalReferencedOn Messages.groupTo
    var replyTo by Message optionalReferencedOn Messages.replyTo

    override var isDeleted by Messages.isDeleted
    override var creationDate by Messages.creationDate
    override var deletionDate by Messages.deletionDate

    val attachments by Attachment via MessagesAttachments
}

