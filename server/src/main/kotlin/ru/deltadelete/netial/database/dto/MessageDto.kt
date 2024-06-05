package ru.deltadelete.netial.database.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.Message

data class MessageDto(
    val text: String,
    val userId: Long,
    val userToId: Long? = null,
    val groupToId: Long? = null,
    val replyToId: Long? = null,
    val isDeleted: Boolean = false,
    val deletionDate: Instant? = null,
    val creationDate: Instant = Clock.System.now(),
    val id: Long = 0L,
) : WebSocketMessage() {
    companion object : MappableDto<Message, MessageDto> {
        override fun from(from: Message): MessageDto {
            return MessageDto(
                from.text,
                from.user.id.value,
                from.userTo?.id?.value,
                from.groupTo?.id?.value,
                from.replyTo?.id?.value,
                from.isDeleted,
                from.deletionDate,
                from.creationDate,
                from.id.value
            )
        }
    }

    @get:JsonIgnore
    val type: MessageType
        get() {
            return when {
                userToId != null && replyToId == null -> MessageType.USER
                groupToId != null && replyToId == null -> MessageType.GROUP
                userToId != null && replyToId != null -> MessageType.USER_REPLY
                groupToId != null && replyToId != null -> MessageType.GROUP_REPLY
                else -> throw Exception("Unknown message type")
            }
        }

    enum class MessageType {
        USER,
        USER_REPLY,
        GROUP,
        GROUP_REPLY
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = MessageDto::class, name = "message"),
    JsonSubTypes.Type(value = WebSocketMessage.Auth::class, name = "auth"),
    JsonSubTypes.Type(value = WebSocketMessage.SystemMessage::class, name = "system")
)
open class WebSocketMessage() {
    data class Auth(val token: String) : WebSocketMessage()
    data class SystemMessage(val text: String): WebSocketMessage()
}