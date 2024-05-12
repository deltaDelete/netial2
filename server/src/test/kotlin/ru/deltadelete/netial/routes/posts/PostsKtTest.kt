package ru.deltadelete.netial.routes.posts

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import ru.deltadelete.netial.database.dto.PostDto
import ru.deltadelete.netial.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PostsKtTest {

    @Test
    fun testGetPosts() = testApplication {
        application {
            module()
        }
        client.get("/posts").apply {
            assertEquals(HttpStatusCode.OK, status, "Unexpected status code")
            assertNotNull(body<List<PostDto>>(), "Unexpected body")
        }
    }
}