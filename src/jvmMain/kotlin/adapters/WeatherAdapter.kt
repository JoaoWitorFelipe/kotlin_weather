package adapters

import kotlinx.serialization.Serializable

@Serializable
data class WeatherAdapter(val main: MainAdapter)

@Serializable
data class MainAdapter(val temp: Float)
