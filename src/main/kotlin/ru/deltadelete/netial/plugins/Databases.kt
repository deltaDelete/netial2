package ru.deltadelete.netial.plugins

import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.deltadelete.netial.database.schemas.*

fun Application.configureDatabases() {
    // TODO from config
    val database = Database.connect(
        url = "jdbc:postgresql://192.168.1.102:5432/ktor?currentSchema=test",
        user = "dev",
        driver = "org.postgresql.Driver",
        password = "devPassword",
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
