package ru.deltadelete.netial.database.schemas

object Messages : DeletableLongIdTable("messages") {
    val text = text("text")
    val user = reference("user_id", Users)
    val userTo = reference("user_to_id", Users).nullable().default(null)
    val groupTo = reference("message_group_id", MessageGroups).nullable().default(null)
    val replyTo = reference("reply_id", Messages).nullable().default(null)
}
