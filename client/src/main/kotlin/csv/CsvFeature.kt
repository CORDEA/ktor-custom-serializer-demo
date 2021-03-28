package csv

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.http.*
import io.ktor.util.*

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
            // TODO
        }

        override fun prepare(block: Config.() -> Unit): CsvFeature {
            val config = Config().apply(block)
            val serializer = config.serializer ?: CsvSerializer()
            return CsvFeature(serializer, config.acceptContentTypes)
        }
    }
}
