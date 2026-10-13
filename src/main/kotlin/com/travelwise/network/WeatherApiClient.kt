package com.travelwise.network

import com.travelwise.model.WeatherData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO internal deskripsi cuaca OpenWeatherMap.
 */
@Serializable
private data class WeatherDescriptionDto(
    @SerialName("main") val main: String,
    @SerialName("description") val description: String
)

/**
 * DTO internal temperatur dan kelembapan OpenWeatherMap.
 */
@Serializable
private data class MainWeatherDto(
    @SerialName("temp") val temp: Double,
    @SerialName("humidity") val humidity: Int
)

/**
 * DTO internal kecepatan angin OpenWeatherMap.
 */
@Serializable
private data class WindDto(
    @SerialName("speed") val speed: Double? = 0.0
)

/**
 * DTO internal utama untuk struktur JSON respons OpenWeatherMap API.
 */
@Serializable
private data class OpenWeatherResponseDto(
    @SerialName("weather") val weather: List<WeatherDescriptionDto>,
    @SerialName("main") val main: MainWeatherDto,
    @SerialName("wind") val wind: WindDto? = null
)

/**
 * Klien jaringan untuk mengunduh kondisi cuaca real-time dari OpenWeatherMap API.
 *
 * @property apiKey API Key OpenWeatherMap. Default dibaca dari *Environment Variable* `OPENWEATHER_API_KEY`.
 * @property client Instance [HttpClient] Ktor yang digunakan untuk mengeksekusi request.
 * @author Farhan
 */
class WeatherApiClient(
    private val apiKey: String = System.getenv("OPENWEATHER_API_KEY") ?: "YOUR_API_KEY_HERE",
    private val client: HttpClient = CountryApiClient.defaultHttpClient
) : ApiClient<WeatherData> {

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }

    /**
     * Mengambil kondisi cuaca terkini berdasarkan nama kota.
     *
     * @param query Nama kota (biasanya ibu kota negara tujuan).
     * @return Objek [WeatherData] jika berhasil, atau `null` jika kota tidak ditemukan / API error.
     */
    override suspend fun fetchData(query: String): WeatherData? {
        if (query.isBlank() || query == "N/A") return null

        return try {
            val response: OpenWeatherResponseDto = client.get(BASE_URL) {
                parameter("q", query)
                parameter("appid", apiKey)
                parameter("units", "metric")
            }.body()

            val primaryWeather = response.weather.firstOrNull()

            WeatherData(
                cityName = query,
                temperature = response.main.temp,
                humidity = response.main.humidity,
                condition = primaryWeather?.main ?: "Clear",
                description = primaryWeather?.description ?: "Tidak ada deskripsi",
                windSpeed = response.wind?.speed ?: 0.0
            )
        } catch (e: ClientRequestException) {
            println("[WeatherApiClient] Client Error (${e.response.status.value}): Kota '$query' tidak ditemukan atau API Key tidak valid.")
            null
        } catch (e: ServerResponseException) {
            println("[WeatherApiClient] Server Error (${e.response.status.value}): Server OpenWeatherMap sedang down.")
            null
        } catch (e: HttpRequestTimeoutException) {
            println("[WeatherApiClient] Timeout: Batas waktu koneksi ke OpenWeatherMap terlampaui.")
            null
        } catch (e: Exception) {
            println("[WeatherApiClient] Error tidak terduga: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Fungsi pembantu spesifik domain untuk mempermudah pemanggilan dari `TravelService`.
     *
     * @param cityName Nama kota lokasi tujuan.
     * @return Objek [WeatherData] atau `null`.
     */
    suspend fun fetchWeather(cityName: String): WeatherData? = fetchData(cityName)
}