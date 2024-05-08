package ru.deltadelete.netial.database.dao

import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import ru.deltadelete.netial.database.dto.UserDto
import ru.deltadelete.netial.database.schemas.Users
import ru.deltadelete.netial.utils.dbQuery

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
            Users.update({ Users.id eq id }) {
                it[isDeleted] = true
                it[deletionDate] = Clock.System.now()
            }
        }
    }
}

