package ru.deltadelete.netial.plugins

import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.schemas.*
import ru.deltadelete.netial.utils.Config

fun Application.configureDatabases() {
    val database = Database.connect(
        url = Config.database.url,
        user = Config.database.user,
        driver = Config.database.driver,
        password = Config.database.password,
        databaseConfig = DatabaseConfig {
            keepLoadedReferencesOutOfTransaction = true
        }
    )
    transaction(database) {
        SchemaUtils.create(
            Attachments,
            Comments,
            CommentsLikes,
            Groups,
            MessageGroups,
            MessageGroupUsers,
            Messages,
            MessagesAttachments,
            Posts,
            PostsAttachments,
            PostsLikes,
            Roles,
            Users,
            UsersGroups,
            UsersRoles
        )
        val logger = KtorSimpleLogger("Database")
        SchemaUtils.checkMappingConsistence().forEach {
            logger.warn(it)
        }
    }
}
