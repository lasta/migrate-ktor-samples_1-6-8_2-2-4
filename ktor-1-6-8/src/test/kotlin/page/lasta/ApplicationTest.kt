package page.lasta

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import page.lasta.plugins.configureRouting
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
        }
    }
}
