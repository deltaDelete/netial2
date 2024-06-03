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
import kotlin.test.assertTrue

class PostsKtTest {

    @Test
    fun testGetPosts() = testApplication {
        application {
            module()
        }
        client.get("/api/posts?page=2&pageSize=10").apply {
            assertEquals(HttpStatusCode.OK, status, "Unexpected status code")
            val body = body<List<PostDto>>()
            assertNotNull(body, "Unexpected body")
            assertTrue { body.size <= 10 } }
        }
    }
}