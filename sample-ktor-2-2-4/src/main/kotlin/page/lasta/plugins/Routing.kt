package page.lasta.plugins

import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources)
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get<Articles> { article ->
            // Get all articles ...
            call.respond("List of articles sorted starting from ${article.sort}")
        }

        post<Articles> {
            val article = runCatching { call.receiveNullable<ArticleBody>() }.getOrNull()
            TODO("do something")
        }
    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")

class ArticleBody(val title: String, val author: String, val body: String)