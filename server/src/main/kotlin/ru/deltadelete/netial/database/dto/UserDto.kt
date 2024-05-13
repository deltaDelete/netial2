package ru.deltadelete.netial.database.dto

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.deltadelete.netial.database.dao.User

open class UserDto(
    val lastName: String,
    val firstName: String,
    val birthDate: Instant,
    val userName: String,
    val email: String,
    val isEmailConfirmed: Boolean,
    val lastLoginDate: Instant,
    val creationDate: Instant,
    val isDeleted: Boolean,
    val deletionDate: Instant? = null,
    val id: Long = 0L,
) {
    companion object : MappableDto<User, UserDto> {
        override fun from(from: User): UserDto {
            return UserDto(
                from.lastName,
                from.firstName,
                from.birthDate,
                from.userName,
                from.email,
                from.isEmailConfirmed,
                from.lastLoginDate,
                from.creationDate,
                from.isDeleted,
                from.deletionDate,
                from.id.value
            )
        }

        fun new(user: UserDto, hash: String): User {
            return User.new {
                lastName = user.lastName
                firstName = user.firstName
                birthDate = user.birthDate
                userName = user.userName
                email = user.email
                isEmailConfirmed = user.isEmailConfirmed
                lastLoginDate = user.lastLoginDate
                passwordHash = hash
                creationDate = user.creationDate
                isDeleted = user.isDeleted
                deletionDate = user.deletionDate
            }
        }
    }

    fun map(it: User) {
        it.lastName = lastName
        it.firstName = firstName
        it.birthDate = birthDate
        it.userName = userName
        it.email = email
        it.isEmailConfirmed = isEmailConfirmed
        it.lastLoginDate = lastLoginDate
        it.creationDate = creationDate
        it.isDeleted = isDeleted
        it.deletionDate = deletionDate
    }
}

class UserRegister(
    lastName: String,
    firstName: String,
    birthDate: Instant,
    userName: String,
    email: String,
    val password: String
) : UserDto(
    lastName,
    firstName,
    birthDate,
    userName,
    email,
    false,
    Clock.System.now(),
    Clock.System.now(),
    false,
    null
)