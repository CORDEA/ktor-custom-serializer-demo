package csv

class Csv {
    fun encodeToString(value: Any): String {
        val csv = value as? List<*> ?: error("")
        return csv.joinToString("\n") { line ->
            (line as? List<*> ?: error(""))
                .joinToString { it as? String ?: error("") }
        }
    }

    fun decodeFromString(string: String): List<List<String>> =
        string.lines().map { it.split("\\s*,\\s*".toRegex()) }
}
