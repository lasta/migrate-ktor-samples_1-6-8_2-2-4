package page.lasta

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import page.lasta.plugins.configureRouting
import page.lasta.plugins.configureSecurity
import page.lasta.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRouting()
}
