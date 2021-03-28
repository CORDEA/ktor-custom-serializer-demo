package csv

import io.ktor.http.*

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
}
