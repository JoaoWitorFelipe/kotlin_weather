package httpClient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class WeatherHttpClient : HttpClient {

    override fun get(endpoint: String): String {
        return URL("https://api.openweathermap.org/data/2.5/$endpoint").readText()
    }
}