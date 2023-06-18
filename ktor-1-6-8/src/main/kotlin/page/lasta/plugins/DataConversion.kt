package page.lasta.plugins

import io.ktor.application.*
import io.ktor.features.*
import java.time.LocalDate

fun Application.configureDataConversion() {
    install(DataConversion) {
        convert<LocalDate> {
            decode { values, _ ->
                values.singleOrNull { it.isNotEmpty() }?.let { LocalDate.parse(it) }
            }
            encode { value ->
                when (value) {
                    null -> emptyList()
                    is LocalDate -> listOf(value.toString())
                    else -> throw IllegalArgumentException("value must be LocalDate. (got: ${value.javaClass.name})")
                }
            }
        }
    }
}

