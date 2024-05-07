package ru.deltadelete.netial.database.schemas

import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import ru.deltadelete.netial.database.schemas.Users.birthDate
import ru.deltadelete.netial.database.schemas.Users.email
import ru.deltadelete.netial.database.schemas.Users.emailNormalized
import ru.deltadelete.netial.database.schemas.Users.firstName
import ru.deltadelete.netial.database.schemas.Users.isEmailConfirmed
import ru.deltadelete.netial.database.schemas.Users.lastLoginDate
import ru.deltadelete.netial.database.schemas.Users.lastName
import ru.deltadelete.netial.database.schemas.Users.passwordHash
import ru.deltadelete.netial.database.schemas.Users.userName

/**
 * Пользователь
 * @property lastName Фамилия
 * @property firstName Имя
 * @property birthDate Дата рождения
 * @property userName Имя пользователя
 * @property email Электронная почта
 * @property emailNormalized Электронная почта нормализованная
 * @property passwordHash Хэш пароля [BCrypt](org.mindrot.jbcrypt.BCrypt)
 * @property isEmailConfirmed Подтверждена ли электронная почта
 * @property lastLoginDate Дата последнего входа
 */
object Users : DeletableLongIdTable("users") {
    val lastName = varchar("last_name", length = 50)
    val firstName = varchar("first_name", length = 50)
    val birthDate = timestamp("birth_date")
    val userName = varchar("user_name", length = 50).uniqueIndex()
    val email = varchar("email", length = 320)
    val emailNormalized = varchar("email_normalized", length = 320).databaseGenerated()
    // BCrypt Hash
    val passwordHash = char("password_hash", length = 60)
    val isEmailConfirmed = bool("is_email_confirmed").default(false)
    val lastLoginDate = timestamp("last_login_date")
}

