package csv

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*

class CsvFeature(
    private val serializer: CsvSerializer,
    private val acceptContentTypes: List<ContentType>
) {
    class Config {
        private val _acceptContentTypes = mutableListOf(ContentType.Text.CSV)

        var acceptContentTypes: List<ContentType>
            set(value) {
                _acceptContentTypes.clear()
                _acceptContentTypes.addAll(value)
            }
            get() = _acceptContentTypes

        var serializer: CsvSerializer? = null
    }

    fun canHandle(contentType: ContentType) =
        acceptContentTypes.any { contentType.match(it) }

    companion object Feature : HttpClientFeature<Config, CsvFeature> {
        override val key: AttributeKey<CsvFeature> = AttributeKey("csv")

        override fun install(feature: CsvFeature, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
                feature.acceptContentTypes.forEach { context.accept(it) }

                val contentType = context.contentType() ?: return@intercept
                if (!feature.canHandle(contentType)) {
                    return@intercept
                }
                context.headers.remove(HttpHeaders.ContentType)

                proceedWith(
                    when (payload) {
                        Unit,
                        is EmptyContent -> EmptyContent
                        else -> feature.serializer.write(payload, contentType)
                    }
                )
            }

            scope.responsePipeline.intercept(HttpRequestPipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel) {
                    return@intercept
                }

                val contentType = context.response.contentType() ?: return@intercept
                if (!feature.canHandle(contentType)) {
                    return@intercept
                }

                proceedWith(
                    HttpResponseContainer(
                        info,
                        feature.serializer.read(info, body.readRemaining())
                    )
                )
            }
        }

        override fun prepare(block: Config.() -> Unit): CsvFeature {
            val config = Config().apply(block)
            val serializer = config.serializer ?: CsvSerializer()
            return CsvFeature(serializer, config.acceptContentTypes)
        }
    }
}
