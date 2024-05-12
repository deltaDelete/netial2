package ru.deltadelete.netial.utils

import com.sun.mail.util.MailConnectException
import io.ktor.http.*
import jakarta.mail.*
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.util.*
import kotlin.collections.set

object Mail {
    val props = Properties().apply {
        this["mail.smtp.host"] = Config.email.host
        this["mail.smtp.port"] = Config.email.port
        this["mail.smtp.ssl.enable"] = "true"
        this["mail.smtp.auth"] = "true"
        this["mail.smtp.starttls.enable"] = "true"
    }

    val emailUsername = Config.email.login
    val emailPassword = Config.email.password

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication() = PasswordAuthentication(emailUsername, emailPassword)
    })

    suspend fun sendEmail(emailMessage: EmailMessage): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val message = MimeMessage(session)
            message.setFrom(emailUsername)
            message.setRecipients(Message.RecipientType.TO, emailMessage.to.lowercase().trim())
            message.subject = emailMessage.subject
            message.sentDate = Calendar.getInstance().time
            message.setHeader(HttpHeaders.ContentType, "text/html; charset=UTF-8")
            message.setContent(emailMessage.message, "text/html; charset=UTF-8")
            Transport.send(message)
            true
        } catch (e: MessagingException) {
            false
        } catch (e: MailConnectException) {
            false
        } catch (e: ConnectException) {
            false
        }
    }

    data class EmailMessage(
        val to: String,
        val subject: String,
        val message: String,
    )
}