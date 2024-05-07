package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.Table

object MessagesAttachments : Table("messages_attachments") {
    val message = reference("message_id", Messages)
    val attachment = reference("attachment_id", Attachments)
    override val primaryKey = PrimaryKey(message, attachment, name = "pk_messages_attachments")
}

