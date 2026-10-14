package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RestCountriesResponse(
    @SerialName("data") val data: RestCountriesData? = null
)

@Serializable
data class RestCountriesData(
    @SerialName("objects") val objects: List<CountryDto> = emptyList()
)

@Serializable
data class CountryDto(
    @SerialName("names") val names: NamesDto? = null,
    @SerialName("capitals") val capitals: List<CapitalDto> = emptyList(),
    @SerialName("population") val population: Long? = 0,
    @SerialName("currencies") val currencies: List<CurrencyDto> = emptyList(),
    @SerialName("flag") val flag: FlagDto? = null,
    @SerialName("codes") val codes: CodesDto? = null
)

@Serializable
data class NamesDto(
    @SerialName("common") val common: String = "",
    @SerialName("official") val official: String = ""
)

@Serializable
data class CapitalDto(
    @SerialName("name") val name: String = ""
)

@Serializable
data class CurrencyDto(
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("symbol") val symbol: String = ""
)

@Serializable
data class FlagDto(
    @SerialName("url_png") val urlPng: String? = null,
    @SerialName("url_svg") val urlSvg: String? = null
)

@Serializable
data class CodesDto(
    @SerialName("alpha_2") val alpha2: String = "",
    @SerialName("alpha_3") val alpha3: String = ""
)

class CountryApiClient(
    private val apiKey: String = System.getenv("RESTCOUNTRIES_API_KEY")
        ?: "rc_live_dab3068ca449486a8d6b4bb00af2acb2",
    private val client: HttpClient = HttpClients.default
) : ApiClient<Country> {

    private companion object {
        const val BASE_URL = "https://api.restcountries.com/countries/v5"

        val ALIASES = mapOf(
            "jepang" to "Japan",
            "inggris" to "United Kingdom",
            "uk" to "United Kingdom",
            "amerika" to "United States",
            "amerika serikat" to "United States",
            "usa" to "United States",
            "jerman" to "Germany",
            "prancis" to "France",
            "perancis" to "France",
            "belanda" to "Netherlands",
            "tiongkok" to "China",
            "cina" to "China",
            "korea" to "South Korea",
            "korea selatan" to "South Korea",
            "rusia" to "Russia",
            "australia" to "Australia",
            "singapura" to "Singapore",
            "malaysia" to "Malaysia",
            "spanyol" to "Spain",
            "italia" to "Italy",
            "swis" to "Switzerland",
            "swiss" to "Switzerland",
            "arab saudi" to "Saudi Arabia"
        )

        val FALLBACK_COUNTRIES = listOf(
            Country("Indonesia", "Jakarta", 273800000L, "IDR", "Indonesian Rupiah"),
            Country("Japan", "Tokyo", 125800000L, "JPY", "Japanese Yen"),
            Country("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar"),
            Country("United Kingdom", "London", 67081000L, "GBP", "British Pound"),
            Country("France", "Paris", 67750000L, "EUR", "Euro"),
            Country("Germany", "Berlin", 83200000L, "EUR", "Euro"),
            Country("Switzerland", "Bern", 8700000L, "CHF", "Swiss Franc"),
            Country("Singapore", "Singapore", 5450000L, "SGD", "Singapore Dollar"),
            Country("Australia", "Canberra", 25690000L, "AUD", "Australian Dollar"),
            Country("Italy", "Rome", 59000000L, "EUR", "Euro")
        )
    }

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun fetchData(query: String): Country? = fetchCountry(query)

    suspend fun fetchCountry(query: String): Country? {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return null

        val mappedQuery = ALIASES[cleanQuery.lowercase()] ?: cleanQuery

        // 1. Coba REST Countries v5 search endpoint (?q=...)
        val searchResult = fetchFromUrl("$BASE_URL?q=${mappedQuery.encodeURLParameter()}")
        if (searchResult != null) return searchResult

        // 2. Offline fallback
        return FALLBACK_COUNTRIES.firstOrNull {
            it.name.equals(mappedQuery, ignoreCase = true) ||
            it.capital.equals(mappedQuery, ignoreCase = true)
        }
    }

    suspend fun fetchByAlpha2(code: String): Country? {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.length != 2) return null

        return fetchFromUrl("$BASE_URL/codes.alpha_2/$cleanCode")
    }

    private suspend fun fetchFromUrl(url: String): Country? {
        return try {
            val response: HttpResponse = client.get(url) {
                header("Authorization", "Bearer $apiKey")
                header(HttpHeaders.Accept, "application/json")
            }

            if (response.status.value != 200) {
                println("[CountryApiClient] REST Countries v5 status ${response.status.value} for $url")
                return null
            }

            val bodyText = response.bodyAsText()
            if (bodyText.isBlank()) return null

            val parsedResponse: RestCountriesResponse = jsonParser.decodeFromString(bodyText)
            val dto = parsedResponse.data?.objects?.firstOrNull() ?: return null

            val name = dto.names?.common?.ifBlank { null } ?: dto.names?.official ?: "Unknown"
            val capital = dto.capitals.firstOrNull()?.name ?: "N/A"
            val currency = dto.currencies.firstOrNull()
            val currencyCode = currency?.code?.ifBlank { null } ?: "USD"
            val currencyName = currency?.name?.ifBlank { null } ?: "United States Dollar"

            Country(
                name = name,
                capital = capital,
                population = dto.population ?: 0L,
                currencyCode = currencyCode,
                currencyName = currencyName,
                officialName = dto.names?.official ?: name,
                flagUrl = dto.flag?.urlPng ?: dto.flag?.urlSvg
            )
        } catch (e: Exception) {
            println("[CountryApiClient] REST Countries v5 error: ${e.localizedMessage}")
            null
        }
    }
}