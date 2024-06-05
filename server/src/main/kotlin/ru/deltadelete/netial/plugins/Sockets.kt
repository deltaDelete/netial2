package ru.deltadelete.netial.plugins

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.jetbrains.exposed.dao.load
import ru.deltadelete.netial.database.dao.Message
import ru.deltadelete.netial.database.dao.MessageGroup
import ru.deltadelete.netial.database.dao.User
import ru.deltadelete.netial.database.dto.MessageDto
import ru.deltadelete.netial.database.dto.WebSocketMessage
import ru.deltadelete.netial.utils.dbQuery
import ru.deltadelete.netial.utils.jsonMapper
import ru.deltadelete.netial.utils.newJsonMapper
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
        val sessions = mutableMapOf<DefaultWebSocketServerSession, Long>()
        webSocket("/api/ws") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        onTextMessage(frame, sessions)
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                sessions.remove(this)
            } catch (e: Throwable) {
                sessions.remove(this)
                throw e
            } finally {
                sessions.remove(this)
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.onTextMessage(
    frame: Frame.Text,
    sessions: MutableMap<DefaultWebSocketServerSession, Long>,
) {
    // TODO loading previous messages
    when (val incoming = jsonMapper.readValue<WebSocketMessage>(frame.readText())) {
        is MessageDto -> onMessageDto(incoming, sessions)
        is WebSocketMessage.Auth -> onAuth(incoming, sessions)
        else -> {}
    }
}

suspend fun DefaultWebSocketServerSession.onMessageDto(
    incoming: MessageDto,
    sessions: Map<DefaultWebSocketServerSession, Long>,
) {
    if (!sessions.containsKey(this)) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
        return
    }

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
            sendSerialized(message)
            sessions.forEach { (t, u) ->
                if (u != message.userToId) {
                    return@forEach
                }
                t.sendSerialized(message)
            }
        }

        MessageDto.MessageType.GROUP, MessageDto.MessageType.GROUP_REPLY -> {
            val group = dbQuery {
                MessageGroup.findById(message.groupToId!!)?.load(MessageGroup::users)
            } ?: throw Exception("Group not found")
            sendSerialized(message)
            group.users.forEach {
                sessions.forEach { (t, u) ->
                    if (u == it.id.value) {
                        try {
                            t.sendSerialized(message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }
}

suspend fun DefaultWebSocketServerSession.onAuth(
    incoming: WebSocketMessage.Auth,
    sessions: MutableMap<DefaultWebSocketServerSession, Long>,
) {
    val principal = authorize(incoming.token)
    val user = dbQuery {
        principal?.subject?.toLong()?.let {
            return@let User.findById(it)
        }
    }
    if (user == null) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
        return
    }
    sessions[this] = user.id.value
    sendSerialized(WebSocketMessage.SystemMessage("Authorized as ${user.id.value}"))
}
