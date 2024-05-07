package ru.deltadelete.netial.routes

import io.ktor.server.application.*
import ru.deltadelete.netial.routes.comments.configureComments
import ru.deltadelete.netial.routes.posts.configurePosts
import ru.deltadelete.netial.routes.users.configureUsers

fun Application.configureRoutes() {
    configureComments()
    configureUsers()
    configurePosts()
}