package csv

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*

class CsvSerializer(
    private val csv: Csv = Csv()
) {
    fun write(data: Any, contentType: ContentType): OutgoingContent =
        TextContent(csv.encodeToString(data), contentType)

    fun read(type: TypeInfo, body: Input): Any {
        val text = body.readText()
        if (type != typeInfo<List<List<String>>>()) {
            error("")
        }
        return csv.decodeFromString(text)
    }
}
