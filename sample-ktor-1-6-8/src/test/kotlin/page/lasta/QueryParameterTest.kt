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


@OptIn(KtorExperimentalLocationsAPI::class)
class QueryParameterTest {

    @Location(PATH)
    @Suppress("unused")
    class TestPrimitiveTypeParameterLocation(
        val intValue: Int,
        val intNullableValue: Int?,
        val intValueWithDefault: Int = 42,
        val intNullableValueWithDefault: Int? = null,
    )

    @ArgumentsSource(TestCaseProvider::class)
    @ParameterizedTest(name = "query: {0}, then returns {2}")
    fun test(
        @Suppress("UNUSED_PARAMETER") description: String,
        queryParameters: List<Pair<String, String>>,
        expected: HttpStatusCode
    ) {
        withHandleRequest(queryParameters) {
            assertEquals(expected, response.status())
        }
    }

    private class TestCaseProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            arguments(
                "When required parameters are not provider", emptyList<Pair<String, String>>(), HttpStatusCode.NotFound
            ),
            arguments(
                "When a required parameter is missing", listOf("intValue" to "1"), HttpStatusCode.NotFound
            ),
            arguments(
                "When required parameters are provided", listOf("intValue" to "1", "intNullableValue" to "2"), HttpStatusCode.OK
            )
        )
    }

    companion object {
        private const val PATH = "/test"
        private fun withHandleRequest(
            queryParameters: List<Pair<String, String>>,
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
            val queryString = queryParameters.joinToString(prefix = "?", separator = "&") { (key, value) ->
                "$key=$value"
            }
            handleRequest(HttpMethod.Get, "$PATH$queryString").run(assertionBlock)
        }
    }
}