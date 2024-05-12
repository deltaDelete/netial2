package ru.deltadelete.netial.database.dao

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.mindrot.jbcrypt.BCrypt
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.schemas.Users
import ru.deltadelete.netial.utils.*
import java.io.File

class UserService {

    suspend fun create(user: UserDto, password: String): Long = dbQuery {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())
        UserDto.new(user, passwordHash).id.value
    }

    suspend fun read(id: Long): User? {
        return dbQuery {
            User.findById(id)
        }
    }

    suspend fun readAll(): List<UserDto> {
        return dbQuery {
            User.find { (Users.deletionDate eq null) and (Users.isDeleted eq false) }
                .map { UserDto.from(it) }
        }
    }

    suspend fun readAll(page: Long = 1, pageSize: Int = 10): List<UserDto> {
        val offset = (page - 1) * pageSize
        return dbQuery {
            User.find { (Users.deletionDate eq null) and (Users.isDeleted eq false) }
                .orderBy(Users.id to SortOrder.ASC)
                .limit(pageSize, offset)
                .map { UserDto.from(it) }
        }
    }

    suspend fun update(id: Long, user: UserDto) {
        return dbQuery {
            User.findByIdAndUpdate(id) {
                user.map(it)
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            User.findByIdAndUpdate(id) {
                it.isDeleted = true
                it.deletionDate = Clock.System.now()
            }
        }
    }

    /**
     * @return true - если письмо было отправлено, иначе false
     */
    suspend fun sendConfirmationEmail(id: Long): Boolean {
        val user = dbQuery {
            User.findById(id)
        }
        if (user == null) {
            return false
        }
        val emailTemplate = File(Config.templates.emailConfirmationTemplate).readText()
        val message = mapOf(
            "username" to user.userName,
            "confirmation" to user.generateConfirmationCode(Config.secret),
            "email" to user.email,
            "name" to "${user.firstName} ${user.lastName}",
            "userId" to user.id.value.toString()
        ).formatTemplate(emailTemplate)

        return Mail.sendEmail(Mail.EmailMessage(user.email, "Confirm email", message))
    }

    /**
     * @return [EmailConfirmResult]
     */
    suspend fun confirmEmail(id: Long, code: String): EmailConfirmResult {
        val user = dbQuery {
            User.findById(id)
        }
        if (user == null) {
            return EmailConfirmResult.USER_NOT_FOUND
        }
        val isValid = user.checkConfirmationCode(code, "secret")
        if (!isValid) {
            return EmailConfirmResult.INVALID_CODE
        }
        dbQuery {
            user.isEmailConfirmed = true
        }
        return EmailConfirmResult.OK
    }

    enum class EmailConfirmResult {
        OK,
        USER_NOT_FOUND,
        INVALID_CODE,
    }
}
