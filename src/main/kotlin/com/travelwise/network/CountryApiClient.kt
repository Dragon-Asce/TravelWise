package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

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
    // Tetap menyimpan API Key yang Anda berikan
    private val apiKey: String = System.getenv("RESTCOUNTRIES_API_KEY")
        ?: "rc_live_dab3068ca449486a8d6b4bb00af2acb2",
    private val client: HttpClient = HttpClients.default
) {
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun fetchCountry(query: String): Country? {
        if (query.isBlank()) return null

        return try {
            val response: HttpResponse = client.get("https://restcountries.com/v3.1/name/$query")

            if (response.status.value != 200) {
                println("[CountryApiClient] REST Countries gagal untuk '$query': HTTP Status ${response.status.value}")
                return null
            }

            val bodyText = response.bodyAsText()

            // Cek apakah respon berupa JsonArray '[' murni
            val jsonElement = jsonParser.parseToJsonElement(bodyText)
            if (jsonElement !is JsonArray) {
                println("[CountryApiClient] REST Countries memberikan JsonObject (Error/Unauthorized) untuk '$query'")
                return null
            }

            val countries: List<CountryDto> = jsonParser.decodeFromJsonElement(jsonElement)
            val dto = countries.firstOrNull() ?: return null

            val currencyCode = dto.currencies?.keys?.firstOrNull() ?: "USD"

            Country(
                name = dto.nameInfo?.common ?: query,
                capital = dto.capitalList?.firstOrNull() ?: "N/A",
                population = dto.population ?: 0,
                currencyCode = currencyCode
            )
        } catch (e: Exception) {
            println("[CountryApiClient] REST Countries gagal untuk '$query': ${e.localizedMessage}")
            null
        }
    }
}