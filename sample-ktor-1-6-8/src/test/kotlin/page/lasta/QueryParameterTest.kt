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
import org.junit.jupiter.params.provider.ArgumentsSources
import java.util.stream.Stream

@OptIn(KtorExperimentalLocationsAPI::class)
class QueryParameterTest {

    @ArgumentsSources(
        ArgumentsSource(RequiredParameterTestCase::class),
        ArgumentsSource(OptionalParameterTestCases::class),
    )
    @ParameterizedTest(name = "{0}, then returns {2}")
    fun test(
        @Suppress("UNUSED_PARAMETER") name: String,
        path: String,
        queryString: String,
        expected: HttpStatusCode,
    ) {
        withHandleRequest(path = path, queryString = queryString) {
            assertEquals(expected, response.status())
        }
    }

    private class RequiredParameterTestCase : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            arguments(
                "When the required parameter is missing",
                REQUIRED_PARAMETER_PATH,
                "",
                HttpStatusCode.NotFound,
            ),
            arguments(
                "When only the key is provided",
                REQUIRED_PARAMETER_PATH,
                "?param",
                HttpStatusCode.BadRequest,
            ),
            arguments(
                "When there is no right-hand side",
                REQUIRED_PARAMETER_PATH,
                "?param=",
                HttpStatusCode.BadRequest,
            ),
            arguments(
                "When the required parameter is provided",
                REQUIRED_PARAMETER_PATH,
                "?param=42",
                HttpStatusCode.OK,
            ),
        )
    }

    private class OptionalParameterTestCases : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            arguments(
                "When the required parameter is missing",
                OPTIONAL_PARAMETER_PATH,
                "",
                HttpStatusCode.OK,
            ),
            arguments(
                "When only the key is provided",
                OPTIONAL_PARAMETER_PATH,
                "?param",
                HttpStatusCode.BadRequest,
            ),
            arguments(
                "When there is no right-hand side",
                OPTIONAL_PARAMETER_PATH,
                "?param=",
                HttpStatusCode.BadRequest,
            ),
            arguments(
                "When the required parameter is provided",
                OPTIONAL_PARAMETER_PATH,
                "?param=42",
                HttpStatusCode.OK,
            ),
        )
    }

    @Location(REQUIRED_PARAMETER_PATH)
    @Suppress("unused")
    class RequiredParameterLocation(
        val param: Int?,
    )

    @Location(OPTIONAL_PARAMETER_PATH)
    @Suppress("unused")
    class OptionalParameterLocation(
        val param: Int? = 42,
    )

    private fun withHandleRequest(
        path: String,
        queryString: String,
        assertionBlock: TestApplicationCall.() -> Unit
    ) {
        withTestApplication(
            {
                install(Locations)
                routing {
                    get<RequiredParameterLocation> {
                        call.respond(HttpStatusCode.OK)
                    }
                    get<OptionalParameterLocation> {
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        ) {
            handleRequest(HttpMethod.Get, "$path$queryString").run(assertionBlock)
        }
    }

    companion object {
        private const val REQUIRED_PARAMETER_PATH = "/test/required-parameter"
        private const val OPTIONAL_PARAMETER_PATH = "/test/optional-parameter"
    }
}