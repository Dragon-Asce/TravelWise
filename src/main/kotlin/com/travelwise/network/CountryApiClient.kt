package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * DTO internal untuk deserialisasi nama negara dari REST Countries API.
 */
@Serializable
private data class CountryNameDto(
    @SerialName("common") val common: String,
    @SerialName("official") val official: String? = null
)

/**
 * DTO internal untuk deserialisasi informasi mata uang dari REST Countries API.
 */
@Serializable
private data class CurrencyDto(
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null
)

/**
 * DTO internal utama penampung struktur JSON respons REST Countries API v3.1.
 */
@Serializable
private data class CountryResponseDto(
    @SerialName("name") val name: CountryNameDto,
    @SerialName("capital") val capital: List<String>? = null,
    @SerialName("population") val population: Long? = 0,
    @SerialName("currencies") val currencies: Map<String, CurrencyDto>? = null
)

/**
 * Klien jaringan untuk mengambil data profil negara dari REST Countries API.
 *
 * Mengimplementasikan [ApiClient] dan mengelola penanganan error HTTP seperti status 404 (tidak ditemukan),
 * 5xx (server error), serta batas waktu koneksi (*timeout*).
 *
 * @property client Instance [HttpClient] Ktor yang dikonfigurasi dengan mesin CIO dan parser JSON.
 * @author Farhan
 */
class CountryApiClient(
    private val client: HttpClient = defaultHttpClient
) : ApiClient<Country> {

    companion object {
        private const val BASE_URL = "https://restcountries.com/v3.1/name"

        /**
         * Konfigurasi standar [HttpClient] Ktor yang dibagikan antar API Client.
         */
        val defaultHttpClient by lazy {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 10000
                    connectTimeoutMillis = 5000
                }
            }
        }
    }

    /**
     * Mengambil detail profil negara berdasarkan nama negara.
     *
     * @param query Nama negara yang dicari.
     * @return Objek [Country] jika ditemukan, atau `null` jika terjadi error / tidak ditemukan.
     */
    override suspend fun fetchData(query: String): Country? {
        if (query.isBlank()) return null

        return try {
            val response: List<CountryResponseDto> = client.get("$BASE_URL/$query").body()
            val dto = response.firstOrNull() ?: return null

            val currencyCode = dto.currencies?.keys?.firstOrNull() ?: "N/A"
            val currencyName = dto.currencies?.get(currencyCode)?.name ?: "Unknown Currency"

            Country(
                name = dto.name.common,
                capital = dto.capital?.firstOrNull() ?: "N/A",
                population = dto.population ?: 0L,
                currencyCode = currencyCode,
                currencyName = currencyName
            )
        } catch (e: ClientRequestException) {
            // Error 4xx (misal 404 Not Found)
            println("[CountryApiClient] Client Error (${e.response.status.value}): Negara '$query' tidak ditemukan.")
            null
        } catch (e: ServerResponseException) {
            // Error 5xx (Server eksternal down)
            println("[CountryApiClient] Server Error (${e.response.status.value}): Layanan REST Countries bermasalah.")
            null
        } catch (e: HttpRequestTimeoutException) {
            println("[CountryApiClient] Timeout: Koneksi ke REST Countries API melebihi batas waktu.")
            null
        } catch (e: Exception) {
            println("[CountryApiClient] Error tidak terduga: ${e.localizedMessage}")
            null
        }
    }

    /**
     * Fungsi pembantu spesifik domain untuk mempermudah pemanggilan dari `TravelService`.
     *
     * @param countryName Nama negara tujuan.
     * @return Objek [Country] atau `null`.
     */
    suspend fun fetchCountry(countryName: String): Country? = fetchData(countryName)
}