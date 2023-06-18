package page.lasta

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.response.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ArgumentsSources
import java.util.stream.Stream

class QueryParameterWithLocationPluginTest {

    @OptIn(KtorExperimentalLocationsAPI::class)
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
        withHandleRequest(path = path, queryString = queryString) { response ->
            Assertions.assertEquals(expected, response.status)
        }
    }

    private class RequiredParameterTestCase : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.arguments(
                "When the required parameter is missing",
                REQUIRED_PARAMETER_PATH,
                "",
                HttpStatusCode.BadRequest,
            ),
            Arguments.arguments(
                "When only the key is provided",
                REQUIRED_PARAMETER_PATH,
                "?param",
                HttpStatusCode.BadRequest,
            ),
            Arguments.arguments(
                "When there is no right-hand side",
                REQUIRED_PARAMETER_PATH,
                "?param=",
                HttpStatusCode.BadRequest,
            ),
            Arguments.arguments(
                "When the required parameter is provided",
                REQUIRED_PARAMETER_PATH,
                "?param=42",
                HttpStatusCode.OK,
            ),
        )
    }

    private class OptionalParameterTestCases : ArgumentsProvider {

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            Arguments.arguments(
                "When the required parameter is missing",
                OPTIONAL_PARAMETER_PATH,
                "",
                HttpStatusCode.OK,
            ),
            Arguments.arguments(
                "When only the key is provided",
                OPTIONAL_PARAMETER_PATH,
                "?param",
                HttpStatusCode.BadRequest,
            ),
            Arguments.arguments(
                "When there is no right-hand side",
                OPTIONAL_PARAMETER_PATH,
                "?param=",
                HttpStatusCode.BadRequest,
            ),
            Arguments.arguments(
                "When the required parameter is provided",
                OPTIONAL_PARAMETER_PATH,
                "?param=42",
                HttpStatusCode.OK,
            ),
        )
    }

    @KtorExperimentalLocationsAPI
    private fun withHandleRequest(
        path: String,
        queryString: String,
        assertionBlock: (HttpResponse) -> Unit,
    ) = testApplication {
        install(Locations)
        routing {
            get<RequiredParameterLocation> {
                call.respond(HttpStatusCode.OK)
            }
            get<OptionalParameterLocation> {
                call.respond(HttpStatusCode.OK)
            }
        }

        assertionBlock(client.get("$path$queryString"))
    }

    @Location(REQUIRED_PARAMETER_PATH)
    class RequiredParameterLocation(
        @Suppress("unused") val param: Int?,
    )

    @Location(OPTIONAL_PARAMETER_PATH)
    @Suppress("unused")
    class OptionalParameterLocation(
        @Suppress("unused") val param: Int? = 42,
    )

    companion object {
        private const val REQUIRED_PARAMETER_PATH = "/test/required-parameter"
        private const val OPTIONAL_PARAMETER_PATH = "/test/optional-parameter"
    }
}