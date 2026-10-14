package com.travelwise.network

import com.travelwise.model.WeatherData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class WeatherDescriptionDto(
    @SerialName("main") val main: String,
    @SerialName("description") val description: String
)

@Serializable
private data class MainWeatherDto(
    @SerialName("temp") val temp: Double,
    @SerialName("humidity") val humidity: Int
)

@Serializable
private data class WindDto(@SerialName("speed") val speed: Double? = 0.0)

@Serializable
private data class CoordDto(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double
)

@Serializable
private data class OpenWeatherResponseDto(
    @SerialName("weather") val weather: List<WeatherDescriptionDto>,
    @SerialName("main") val main: MainWeatherDto,
    @SerialName("wind") val wind: WindDto? = null,
    @SerialName("coord") val coord: CoordDto? = null
)

/**
 * Cuaca real-time OpenWeatherMap. Sekarang juga mengembalikan koordinat (lat/lon)
 * yang dipakai Overpass API untuk mencari tempat di sekitar kota.
 */
class WeatherApiClient(
    private val apiKey: String = System.getenv("OPENWEATHER_API_KEY") ?: "",
    private val client: HttpClient = HttpClients.default
) : ApiClient<WeatherData> {

    private companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }

    override suspend fun fetchData(query: String): WeatherData? {
        if (query.isBlank() || query == "N/A") return null

        return try {
            val response: OpenWeatherResponseDto = client.get(BASE_URL) {
                parameter("q", query)
                parameter("appid", apiKey)
                parameter("units", "metric")
            }.body()

            val primary = response.weather.firstOrNull()

            WeatherData(
                cityName = query,
                temperature = response.main.temp,
                humidity = response.main.humidity,
                condition = primary?.main ?: "Clear",
                description = primary?.description ?: "Tidak ada deskripsi",
                windSpeed = response.wind?.speed ?: 0.0,
                lat = response.coord?.lat,
                lon = response.coord?.lon
            )
        } catch (e: ClientRequestException) {
            println("[WeatherApiClient] Client Error (${e.response.status.value}): Kota '$query' tidak ditemukan atau API Key tidak valid.")
            null
        } catch (e: ServerResponseException) {
            println("[WeatherApiClient] Server Error (${e.response.status.value})")
            null
        } catch (e: HttpRequestTimeoutException) {
            println("[WeatherApiClient] Timeout ke OpenWeatherMap.")
            null
        } catch (e: Exception) {
            println("[WeatherApiClient] Error tidak terduga: ${e.localizedMessage}")
            null
        }
    }

    suspend fun fetchWeather(cityName: String): WeatherData? = fetchData(cityName)
}