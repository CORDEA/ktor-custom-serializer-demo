import csv.csv
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.main() {
    install(ContentNegotiation) {
        csv()
    }

    routing {
        get("/api") {
            val csv = listOf(listOf("a", "b", "c"), listOf("d", "e", "f"))
            call.respond(HttpStatusCode.OK, csv)
        }

        post("/api") {
            val csv = call.receive<List<List<String>>>()
            println(csv)
            call.respond(HttpStatusCode.OK)
        }
    }
}
