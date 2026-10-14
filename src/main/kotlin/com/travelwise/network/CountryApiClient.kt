package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * REST Countries v3.1 + kamus alias/offline fallback.
 * Alur: alias -> nama kanonik -> API -> kamus -> anggap input sebagai kota.
 */
class CountryApiClient(
    private val baseUrl: String = "https://restcountries.com/v3.1",
    private val client: HttpClient = HttpClients.default
) : ApiClient<Country> {

    private fun c(name: String, capital: String, pop: Long, cur: String, curName: String, cca2: String) =
        Country(
            name = name, capital = capital, population = pop,
            currencyCode = cur, currencyName = curName, officialName = name,
            flagUrl = "https://flagcdn.com/w160/${cca2.lowercase()}.png"
        )

    private val japan = c("Japan", "Tokyo", 125800000L, "JPY", "Japanese Yen", "JP")
    private val indonesia = c("Indonesia", "Jakarta", 273800000L, "IDR", "Indonesian Rupiah", "ID")
    private val france = c("France", "Paris", 67750000L, "EUR", "Euro", "FR")
    private val usa = c("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar", "US")
    private val uk = c("United Kingdom", "London", 67081000L, "GBP", "British Pound", "GB")
    private val germany = c("Germany", "Berlin", 83200000L, "EUR", "Euro", "DE")
    private val italy = c("Italy", "Rome", 59000000L, "EUR", "Euro", "IT")
    private val singapore = c("Singapore", "Singapore", 5450000L, "SGD", "Singapore Dollar", "SG")
    private val malaysia = c("Malaysia", "Kuala Lumpur", 32700000L, "MYR", "Malaysian Ringgit", "MY")
    private val korea = c("South Korea", "Seoul", 51740000L, "KRW", "South Korean Won", "KR")
    private val china = c("China", "Beijing", 1412000000L, "CNY", "Chinese Yuan", "CN")
    private val australia = c("Australia", "Canberra", 25690000L, "AUD", "Australian Dollar", "AU")
    private val thailand = c("Thailand", "Bangkok", 71600000L, "THB", "Thai Baht", "TH")
    private val netherlands = c("Netherlands", "Amsterdam", 17530000L, "EUR", "Euro", "NL")

    private val defaultCountries: Map<String, Country> = mapOf(
        "japan" to japan, "jepang" to japan,
        "indonesia" to indonesia,
        "france" to france, "prancis" to france,
        "united states" to usa, "usa" to usa, "amerika" to usa, "amerika serikat" to usa,
        "united kingdom" to uk, "uk" to uk, "inggris" to uk,
        "germany" to germany, "jerman" to germany,
        "italy" to italy, "italia" to italy,
        "singapore" to singapore, "singapura" to singapore,
        "malaysia" to malaysia,
        "south korea" to korea, "korea" to korea, "korea selatan" to korea,
        "china" to china, "tiongkok" to china,
        "australia" to australia,
        "thailand" to thailand,
        "netherlands" to netherlands, "belanda" to netherlands
    )

    override suspend fun fetchData(query: String): Country? = fetchCountry(query)

    suspend fun fetchCountry(name: String): Country? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null

        val seed = defaultCountries[trimmed.lowercase()]
        val searchName = seed?.name ?: trimmed

        fetchFromApi(searchName)?.let { return it }
        if (seed != null) return seed

        // Fallback umum: anggap input sebagai nama kota tujuan
        val city = trimmed.replaceFirstChar { it.uppercase() }
        return Country(
            name = city, capital = city, population = 0L,
            currencyCode = "USD", currencyName = "United States Dollar"
        )
    }

    private suspend fun fetchFromApi(searchName: String): Country? = try {
        val text = client.get(baseUrl) {
            url { appendPathSegments("name", searchName) }
            parameter("fields", "name,capital,population,currencies,flags,cca2")
        }.bodyAsText()

        val array = Json.parseToJsonElement(text).jsonArray
        val item = (array.firstOrNull {
            it.jsonObject["name"]?.jsonObject?.get("common")?.jsonPrimitive?.contentOrNull
                .equals(searchName, ignoreCase = true)
        } ?: array.firstOrNull())?.jsonObject

        if (item == null) null else {
            val nameObj = item["name"]?.jsonObject
            val common = nameObj?.get("common")?.jsonPrimitive?.contentOrNull ?: searchName
            val official = nameObj?.get("official")?.jsonPrimitive?.contentOrNull ?: common
            val capital = item["capital"]?.jsonArray?.firstOrNull()?.jsonPrimitive?.contentOrNull ?: "N/A"
            val population = item["population"]?.jsonPrimitive?.longOrNull ?: 0L
            val currencies = item["currencies"]?.jsonObject
            val code = currencies?.keys?.firstOrNull() ?: "USD"
            val curName = currencies?.get(code)?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull ?: code
            val flag = item["flags"]?.jsonObject?.get("png")?.jsonPrimitive?.contentOrNull
                ?: item["cca2"]?.jsonPrimitive?.contentOrNull?.let { "https://flagcdn.com/w160/${it.lowercase()}.png" }

            Country(common, capital, population, code, curName, official, flag)
        }
    } catch (e: Exception) {
        println("[CountryApiClient] REST Countries gagal untuk '$searchName': ${e.message}")
        null
    }
}