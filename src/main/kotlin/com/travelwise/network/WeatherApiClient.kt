package com.travelwise.network

import com.travelwise.model.WeatherData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class WeatherDescriptionDto(
    @SerialName("main") val main: String = "Clear",
    @SerialName("description") val description: String = "Tidak ada deskripsi"
)

@Serializable
private data class MainWeatherDto(
    @SerialName("temp") val temp: Double = 0.0,
    @SerialName("humidity") val humidity: Int = 0
)

@Serializable
private data class WindDto(
    @SerialName("speed") val speed: Double? = 0.0
)

@Serializable
private data class CoordDto(
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lon") val lon: Double? = null
)

@Serializable
private data class SysDto(
    @SerialName("country") val country: String? = null
)

@Serializable
private data class OpenWeatherResponseDto(
    @SerialName("weather") val weather: List<WeatherDescriptionDto>? = emptyList(),
    @SerialName("main") val main: MainWeatherDto? = null,
    @SerialName("wind") val wind: WindDto? = null,
    @SerialName("coord") val coord: CoordDto? = null,
    @SerialName("sys") val sys: SysDto? = null,
    @SerialName("name") val name: String? = null
)

class WeatherApiClient(
    private val apiKey: String = System.getenv("OPENWEATHER_API_KEY")
        ?: "776e0acd12aee173a3abece2c9a46e7e",
    private val client: HttpClient = HttpClients.default
) : ApiClient<WeatherData> {

    private companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }

    override suspend fun fetchData(query: String): WeatherData? {
        if (query.isBlank() || query == "N/A") return null

        return try {
            val httpResponse: HttpResponse = client.get(BASE_URL) {
                parameter("q", query)
                parameter("appid", apiKey.ifBlank { "776e0acd12aee173a3abece2c9a46e7e" })
                parameter("units", "metric")
            }

            if (httpResponse.status.value != 200) {
                println("[WeatherApiClient] Client Error (${httpResponse.status.value}): Kota '$query' tidak ditemukan atau API Key belum aktif.")
                return null
            }

            val response: OpenWeatherResponseDto = httpResponse.body()
            val mainInfo = response.main ?: return null
            val primary = response.weather?.firstOrNull()

            WeatherData(
                cityName = response.name ?: query,
                temperature = mainInfo.temp,
                humidity = mainInfo.humidity,
                condition = primary?.main ?: "Clear",
                description = primary?.description ?: "Tidak ada deskripsi",
                windSpeed = response.wind?.speed ?: 0.0,
                lat = response.coord?.lat,
                lon = response.coord?.lon,
                countryCode = response.sys?.country
            )
        } catch (e: Exception) {
            println("[WeatherApiClient] Error: ${e.localizedMessage}")
            null
        }
    }

    suspend fun fetchWeather(cityName: String): WeatherData? = fetchData(cityName)
}