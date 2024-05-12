package ru.deltadelete.netial.plugins

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.jetbrains.exposed.dao.load
import ru.deltadelete.netial.database.dao.Message
import ru.deltadelete.netial.database.dao.MessageGroup
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.MessageDto
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.jsonMapper
import ru.deltadelete.netial.utils.newJsonMapper
import ru.deltadelete.netial.utils.principalUser
import java.time.Duration

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = JacksonWebsocketContentConverter(newJsonMapper())
    }
    routing {
        val sessions = mutableMapOf<Long,  WebSocketServerSession>()
        authenticate("auth-jwt") {
            webSocket("/ws") {
                val user = principalUser()
                if (user == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                    return@webSocket
                }
                sessions[user.id.value] = this

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            onTextMessage(frame, sessions)
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    sessions.remove(user.id.value)
                } catch (e: Throwable) {
                    sessions.remove(user.id.value)
                }
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.onTextMessage(frame: Frame.Text, sessions: Map<Long,  WebSocketServerSession>) {
    // TODO loading previous messages
    val incoming = jsonMapper.readValue<MessageDto>(frame.readText())

    val message = dbQuery {
        Message.new {
            text = incoming.text
            user = User.findById(incoming.userId) ?: return@new
            userTo = incoming.userToId?.let { User.findById(it) }
            groupTo = incoming.groupToId?.let { MessageGroup.findById(it) }
            replyTo = incoming.replyToId?.let { Message.findById(it) }
            isDeleted = incoming.isDeleted
            deletionDate = incoming.deletionDate
            creationDate = incoming.creationDate
        }
    }.let {
        MessageDto.from(it)
    }

    when (message.type) {
        MessageDto.MessageType.USER, MessageDto.MessageType.USER_REPLY -> {
            // sessions[message.userToId!!]?.send(JsonFrame(message))
            sessions[message.userToId!!]?.sendSerialized(message)
        }
        MessageDto.MessageType.GROUP, MessageDto.MessageType.GROUP_REPLY  -> {
            val group = dbQuery {
                MessageGroup.findById(message.groupToId!!)?.load(MessageGroup::users)
            } ?: throw Exception("Group not found")
            group.users.forEach {
                sessions[it.id.value]?.sendSerialized(message)
            }
        }
    }
}
