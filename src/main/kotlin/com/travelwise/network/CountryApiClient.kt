package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class CountryDto(
    @SerialName("name") val nameInfo: NameDto? = null,
    @SerialName("capital") val capitalList: List<String>? = emptyList(),
    @SerialName("population") val population: Long? = 0,
    @SerialName("currencies") val currencies: Map<String, CurrencyDto>? = emptyMap()
)

@Serializable
data class NameDto(
    @SerialName("common") val common: String? = ""
)

@Serializable
data class CurrencyDto(
    @SerialName("name") val name: String? = "",
    @SerialName("symbol") val symbol: String? = ""
)

class CountryApiClient(
    @Suppress("unused")
    private val apiKey: String = System.getenv("RESTCOUNTRIES_API_KEY")
        ?: "rc_live_dab3068ca449486a8d6b4bb00af2acb2",
    private val client: HttpClient = HttpClients.default
) {
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun fetchCountry(query: String): Country? {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return null

        // 1. Coba Endpoint Nama Utama (/v3.1/name/)
        // 2. Jika tidak ditemukan (404), coba Endpoint Terjemahan (/v3.1/translation/)
        return fetchFromUrl("https://restcountries.com/v3.1/name/$cleanQuery")
            ?: fetchFromUrl("https://restcountries.com/v3.1/translation/$cleanQuery")
    }

    private suspend fun fetchFromUrl(url: String): Country? {
        return try {
            val response: HttpResponse = client.get(url) {
                header(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                header(HttpHeaders.Accept, "application/json")
            }

            val bodyText = response.bodyAsText()
            if (bodyText.isBlank()) return null

            val jsonElement = jsonParser.parseToJsonElement(bodyText)

            val dto: CountryDto = when (jsonElement) {
                is JsonArray -> {
                    if (jsonElement.isEmpty()) return null
                    jsonParser.decodeFromJsonElement(jsonElement.first())
                }
                is JsonObject -> {
                    // Cek jika JsonObject berisi respon error dari server (seperti {"status": 404, "message": "Not Found"})
                    if (jsonElement.containsKey("status") || jsonElement.containsKey("message")) {
                        val msg = jsonElement["message"]?.jsonPrimitive?.content ?: "Error Response"
                        val status = jsonElement["status"]?.jsonPrimitive?.content ?: response.status.value.toString()
                        println("[CountryApiClient] REST Countries Info [$status]: $msg")
                        return null
                    }
                    // Cek jika JsonObject adalah data negara tunggal
                    jsonParser.decodeFromJsonElement(jsonElement)
                }
                else -> return null
            }

            val currencyCode = dto.currencies?.keys?.firstOrNull() ?: "USD"

            Country(
                name = dto.nameInfo?.common ?: "Unknown",
                capital = dto.capitalList?.firstOrNull() ?: "N/A",
                population = dto.population ?: 0,
                currencyCode = currencyCode
            )
        } catch (e: Exception) {
            println("[CountryApiClient] REST Countries Error: ${e.localizedMessage}")
            null
        }
    }
}