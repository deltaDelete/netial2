package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.*

/**
 * Пользователь
 * @property lastName Фамилия
 * @property firstName Имя
 * @property birthDate Дата рождения
 * @property userName Имя пользователя
 * @property email Электронная почта
 * @property emailNormalized Электронная почта нормализованная
 * @property passwordHash Хэш пароля
 * @property isEmailConfirmed Признак подтверждения почты
 * @property lastLoginDate Дата последнего входа
 * @property messageGroups Группы сообщений, в которых состоит пользователь
 * @property groups Группы, в которых состоит пользователь
 * @property roles Роли пользователя
 */
class User(id: EntityID<Long>) : LongEntity(id), DeletableEntity {
    companion object : LongEntityClass<User>(Users)

    var lastName by Users.lastName
    var firstName by Users.firstName
    var birthDate by Users.birthDate
    var userName by Users.userName
    var email by Users.email
    var emailNormalized by Users.emailNormalized
    var passwordHash by Users.passwordHash
    var isEmailConfirmed by Users.isEmailConfirmed
    var lastLoginDate by Users.lastLoginDate

    override var isDeleted by Users.isDeleted
    override var creationDate by Users.creationDate
    override var deletionDate by Users.deletionDate

    val roles by Role via UsersRoles
    val messageGroups by MessageGroup via MessageGroupUsers
    val ownedMessageGroups by MessageGroup referrersOn MessageGroups.creator
    val groups by Group via UsersGroups
}

fun User.Companion.findByUserName(name: String): User? {
    return testCache { this.userName == name }.firstOrNull() ?: find { Users.userName eq name }.firstOrNull()
}
