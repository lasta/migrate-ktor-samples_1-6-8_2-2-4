@file:OptIn(KtorExperimentalLocationsAPI::class)

package page.lasta

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import page.lasta.plugins.configureDataConversion
import java.util.stream.Stream


class QueryParameterTest {

    @Location(PATH)
    @Suppress("unused")
    class TestPrimitiveTypeParameterLocation(val intValue: Int = 42, val intNullableValue: Int?)

    @ArgumentsSource(TestCaseProvider::class)
    @ParameterizedTest(name = "query: {0}, expected: {1}")
    fun test(queryString: String, expected: HttpStatusCode) {
        withHandleRequest(queryString) {
            assertEquals(expected, response.status())
        }
    }

    private class TestCaseProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            arguments(
                "", HttpStatusCode.BadRequest
            ),
        )
    }

    companion object {
        private const val PATH = "/test"
        private fun withHandleRequest(
            queryString: String,
            assertionBlock: TestApplicationCall.() -> Unit
        ) = withTestApplication(
            {
                configureDataConversion()
                install(Locations)
                routing {
                    get<TestPrimitiveTypeParameterLocation> {
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        ) {
            with(handleRequest(HttpMethod.Get, "$PATH?$queryString")) {
                assertionBlock()
            }
        }
    }
}