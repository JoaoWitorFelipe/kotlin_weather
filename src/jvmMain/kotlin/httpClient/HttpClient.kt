package httpClient

interface HttpClient {
    fun get(endpoint: String): String
}