import io.ktor.application.*
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import dev.jbs.ktor.thymeleaf.*
import io.ktor.http.content.*
import io.ktor.request.receiveMultipart
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.wycliffeassociates.sourceaudio.upload.RequestHandler

fun Application.module() {
    install(DefaultHeaders)
    install(CORS) {
        anyHost()
        header(HttpHeaders.AccessControlAllowOrigin)
    }
    install(CallLogging)
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    install(Routing) {
        routing {
            static("static") {
                files("src/css")
                files("src/js")
            }
        }
        get("/") {
            call.respond(ThymeleafContent("index", mapOf("obj" to "")))
        }
        post("/upload") {
            val multiPart = call.receiveMultipart().readAllParts()
            val result = RequestHandler().handleFormUpload(multiPart)

            call.respondText(result, ContentType.Application.Json)
        }
    }
}