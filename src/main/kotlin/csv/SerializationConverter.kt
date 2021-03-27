package csv

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlin.text.Charsets

class SerializationConverter(
    private val csv: Csv,
    private val defaultCharset: Charset = Charsets.UTF_8
) : ContentConverter {
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val channel = context.subject.value as? ByteReadChannel ?: return null
        val charset = context.call.request.contentCharset() ?: defaultCharset
        val content = channel.readRemaining()
        return csv.decodeFromString(content.readText(charset))
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any {
        val content = csv.encodeToString(value)
        return TextContent(content, contentType.withCharset(context.call.suitableCharset()))
    }
}

fun ContentNegotiation.Configuration.csv(
    csv: Csv = Csv(),
    contentType: ContentType = ContentType.Text.CSV
) {
    register(
        contentType,
        SerializationConverter(csv)
    )
}
