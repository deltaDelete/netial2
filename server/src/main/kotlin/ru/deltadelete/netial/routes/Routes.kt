package ru.deltadelete.netial.routes

import io.ktor.server.application.*
import ru.deltadelete.netial.routes.attachments.configureAttachments
import ru.deltadelete.netial.routes.comments.configureComments
import ru.deltadelete.netial.routes.groups.configureGroups
import ru.deltadelete.netial.routes.messages.configureMessages
import ru.deltadelete.netial.routes.posts.configurePosts
import ru.deltadelete.netial.routes.roles.configureRoles
import ru.deltadelete.netial.routes.users.configureUsers

fun Application.configureRoutes() {
    configureComments()
    configureUsers()
    configurePosts()
    configureGroups()
    configureRoles()
    configureAttachments()
    configureMessages()
}