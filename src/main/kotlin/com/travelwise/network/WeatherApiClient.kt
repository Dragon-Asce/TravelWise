package com.travelwise.network

import com.travelwise.model.WeatherData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) untuk kondisi umum dan ringkasan cuaca OpenWeather.
 *
 * @property main Kategori cuaca utama (contoh: "Clear", "Rain", "Clouds", "Snow").
 * @property description Deskripsi detail cuaca (contoh: "light rain", "overcast clouds").
 * @author Farhan
 */
@Serializable
private data class WeatherDescriptionDto(
    @SerialName("main") val main: String = "Clear",
    @SerialName("description") val description: String = "Tidak ada deskripsi"
)

/**
 * Data Transfer Object (DTO) untuk informasi suhu dan kelembapan udara.
 *
 * @property temp Nilai temperatur suhu dalam derajat Celsius.
 * @property humidity Persentase tingkat kelembapan udara (0 - 100%).
 */
@Serializable
private data class MainWeatherDto(
    @SerialName("temp") val temp: Double = 0.0,
    @SerialName("humidity") val humidity: Int = 0
)

/**
 * Data Transfer Object (DTO) untuk informasi kecepatan angin.
 *
 * @property speed Kecepatan hembusan angin dalam meter per detik (m/s).
 */
@Serializable
private data class WindDto(
    @SerialName("speed") val speed: Double? = 0.0
)

/**
 * Data Transfer Object (DTO) untuk koordinat geografis lokasi perkiraan cuaca.
 *
 * @property lat Garis lintang (*latitude*) lokasi.
 * @property lon Garis bujur (*longitude*) lokasi.
 */
@Serializable
private data class CoordDto(
    @SerialName("lat") val lat: Double? = null,
    @SerialName("lon") val lon: Double? = null
)

/**
 * Data Transfer Object (DTO) untuk informasi sistem internal OpenWeather.
 *
 * @property country Kode negara dua huruf standar ISO 3166-1 alpha-2 tempat kota berada (contoh: "ID", "JP").
 */
@Serializable
private data class SysDto(
    @SerialName("country") val country: String? = null
)

/**
 * Data Transfer Object (DTO) representasi root dari respons OpenWeather Current Weather Data API.
 *
 * @property weather Daftar deskripsi kondisi cuaca.
 * @property main Data utama suhu dan kelembapan.
 * @property wind Informasi kecepatan angin.
 * @property coord Koordinat lintang dan bujur kota.
 * @property sys Metadata sistem termasuk kode negara.
 * @property name Nama resmi kota hasil resolusi OpenWeather.
 */
@Serializable
private data class OpenWeatherResponseDto(
    @SerialName("weather") val weather: List<WeatherDescriptionDto>? = emptyList(),
    @SerialName("main") val main: MainWeatherDto? = null,
    @SerialName("wind") val wind: WindDto? = null,
    @SerialName("coord") val coord: CoordDto? = null,
    @SerialName("sys") val sys: SysDto? = null,
    @SerialName("name") val name: String? = null
)

/**
 * Klien HTTP untuk berinteraksi dengan OpenWeather Current Weather Data API (`https://api.openweathermap.org/data/2.5/weather`).
 *
 * Mengimplementasikan antarmuka [ApiClient] bertipe [WeatherData] untuk menyediakan telemetri cuaca
 * secara *real-time* berdasarkan nama kota destinasi perjalanan.
 *
 * Peran dalam arsitektur aplikasi:
 * - Menyediakan metrik suhu (°C), kelembapan (%), kondisi awan/hujan, dan kecepatan angin.
 * - Mengekstrak koordinat geografis (`lat`/`lon`) yang menjadi input bagi pencarian tempat wisata di [OverpassApiClient].
 * - Menyediakan kode negara ISO 2-huruf (`countryCode`) yang memungkinkan mekanisme resolusi cerdas kota-ke-negara di [com.travelwise.service.TravelService].
 *
 * @property apiKey Kunci otentikasi API yang dibaca dari variabel lingkungan `OPENWEATHER_API_KEY`.
 * @property client Instance [HttpClient] bersama yang digunakan untuk eksekusi panggilan HTTP.
 * @author Farhan
 */
class WeatherApiClient(
    private val apiKey: String = System.getenv("OPENWEATHER_API_KEY")
        ?: "776e0acd12aee173a3abece2c9a46e7e",
    private val client: HttpClient = HttpClients.default
) : ApiClient<WeatherData> {

    private companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }

    /**
     * Mengambil data telemetri cuaca terkini untuk kota tertentu (implementasi [ApiClient]).
     *
     * Permintaan dikirim dengan parameter `units=metric` agar besaran temperatur dinyatakan dalam Celsius.
     *
     * @param query Nama kota yang ingin diperiksa cuacanya (contoh: "Tokyo", "Bandung", "Paris").
     * @return Entitas domain [WeatherData] jika kota ditemukan dan API berhasil merespons, atau `null` jika gagal.
     */
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

    /**
     * Fungsi alias untuk mengambil data cuaca kota tujuan secara eksplisit.
     *
     * @param cityName Nama kota destinasi perjalanan.
     * @return Objek [WeatherData] hasil pengamatan, atau `null` jika terjadi kegagalan.
     */
    suspend fun fetchWeather(cityName: String): WeatherData? = fetchData(cityName)
}