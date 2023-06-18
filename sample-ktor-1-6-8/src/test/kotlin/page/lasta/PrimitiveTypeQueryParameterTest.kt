package page.lasta

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import page.lasta.plugins.configureDataConversion

@OptIn(KtorExperimentalLocationsAPI::class)
class PrimitiveTypeQueryParameterTest {

    @Location(PATH)
    @Suppress("unused")
    class WithoutDefaultValuesLocation(
        val intValue: Int,
    )

    @Nested
    inner class RequiredParameterTest {

        private fun withHandleRequest(
            queryParameters: List<Pair<String, String>>,
            block: TestApplicationCall.() -> Unit
        ) {
            withTestApplication(
                {
                    configureDataConversion()
                    install(Locations)
                    routing {
                        get<WithoutDefaultValuesLocation> {
                            call.respond(HttpStatusCode.OK)
                        }
                    }
                }
            ) {
                val queryString = queryParameters.joinToString(prefix = "?", separator = "&") { (key, value) ->
                    "$key=$value"
                }
                handleRequest(HttpMethod.Get, "$PATH$queryString").run(block)
            }
        }

        @Test
        fun `when a required parameter is missing then returns NotFound`() {
            withHandleRequest(emptyList()) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }

        @Test
        fun `when a required parameter is provided then returns OK`() {
            withHandleRequest(listOf("intValue" to "1")) {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    companion object {
        private const val PATH = "/test"
    }
}