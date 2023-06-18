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
        val intValue: Int?,
    )

    @Nested
    inner class RequiredParameterTest {

        private fun withHandleRequest(
            queryString: String,
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
                handleRequest(HttpMethod.Get, "$PATH$queryString").run(block)
            }
        }

        @Test
        fun `when a required parameter is missing then returns NotFound`() {
            withHandleRequest("") {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }

        @Test
        fun `when a required parameter is provided then returns OK`() {
            withHandleRequest("?intValue=42") {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    companion object {
        private const val PATH = "/test"
    }
}