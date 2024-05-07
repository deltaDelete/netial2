package ru.deltadelete.netial.database.dao

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.deltadelete.netial.database.schemas.Users

class User(id: EntityID<Long>) : LongEntity(id) {
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

    var isDeleted by Users.isDeleted
    var creationDate by Users.creationDate
    var deletionDate by Users.deletionDate

}

fun User.Companion.findByUserName(name: String): User? {
    return testCache { this.userName == name }.firstOrNull() ?: find { Users.userName eq name }.firstOrNull()
}
