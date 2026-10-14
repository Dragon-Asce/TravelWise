package com.travelwise.network

import com.travelwise.model.Country
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import java.time.Duration
import kotlinx.serialization.json.*

class CountryApiClient(
    private val baseUrl: String = "https://api.restcountries.com/countries/v5",
    private val apiKey: String = System.getenv("RESTCOUNTRIES_API_KEY") ?: ""
) {
    private val client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(6))
        .build()

    // Kamus negara populer jika API eksternal unreachable / rate-limited
    private val defaultCountries = mapOf(
        "japan" to Country("Japan", "Tokyo", 125800000L, "JPY", "Japanese Yen"),
        "jepang" to Country("Japan", "Tokyo", 125800000L, "JPY", "Japanese Yen"),
        "indonesia" to Country("Indonesia", "Jakarta", 273800000L, "IDR", "Indonesian Rupiah"),
        "france" to Country("France", "Paris", 67750000L, "EUR", "Euro"),
        "prancis" to Country("France", "Paris", 67750000L, "EUR", "Euro"),
        "united states" to Country("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar"),
        "usa" to Country("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar"),
        "amerika" to Country("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar"),
        "united kingdom" to Country("United Kingdom", "London", 67081000L, "GBP", "British Pound"),
        "uk" to Country("United Kingdom", "London", 67081000L, "GBP", "British Pound"),
        "inggris" to Country("United Kingdom", "London", 67081000L, "GBP", "British Pound"),
        "germany" to Country("Germany", "Berlin", 83200000L, "EUR", "Euro"),
        "jerman" to Country("Germany", "Berlin", 83200000L, "EUR", "Euro"),
        "italy" to Country("Italy", "Rome", 59000000L, "EUR", "Euro"),
        "italia" to Country("Italy", "Rome", 59000000L, "EUR", "Euro"),
        "singapore" to Country("Singapore", "Singapore", 5450000L, "SGD", "Singapore Dollar"),
        "singapura" to Country("Singapore", "Singapore", 5450000L, "SGD", "Singapore Dollar"),
        "malaysia" to Country("Malaysia", "Kuala Lumpur", 32700000L, "MYR", "Malaysian Ringgit"),
        "south korea" to Country("South Korea", "Seoul", 51740000L, "KRW", "South Korean Won"),
        "korea" to Country("South Korea", "Seoul", 51740000L, "KRW", "South Korean Won"),
        "korea selatan" to Country("South Korea", "Seoul", 51740000L, "KRW", "South Korean Won"),
        "china" to Country("China", "Beijing", 1412000000L, "CNY", "Chinese Yuan"),
        "tiongkok" to Country("China", "Beijing", 1412000000L, "CNY", "Chinese Yuan"),
        "australia" to Country("Australia", "Canberra", 25690000L, "AUD", "Australian Dollar"),
        "thailand" to Country("Thailand", "Bangkok", 71600000L, "THB", "Thai Baht"),
        "netherlands" to Country("Netherlands", "Amsterdam", 17530000L, "EUR", "Euro"),
        "belanda" to Country("Netherlands", "Amsterdam", 17530000L, "EUR", "Euro")
    )

    fun fetchCountry(name: String): Country? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null

        // 1. Prioritas Utama: REST Countries API (Mendukung v5 & legacy v3.1)
        try {
            val encodedName = java.net.URLEncoder.encode(trimmed, "UTF-8")
            val requestUri = if (baseUrl.contains("/v5")) {
                if (apiKey.isNotBlank()) "$baseUrl/names.common/$encodedName?api-key=$apiKey"
                else "$baseUrl/names.common/$encodedName"
            } else {
                "$baseUrl/name/$encodedName"
            }

            val requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .header("User-Agent", "TravelWise/1.0")
                .timeout(Duration.ofSeconds(5))
                .GET()

            if (apiKey.isNotBlank()) {
                requestBuilder.header("Authorization", "Bearer $apiKey")
            }

            val response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == 200) {
                val jsonElement = Json.parseToJsonElement(response.body())

                // Format REST Countries v5: { "data": { "objects": [ ... ] } }
                if (jsonElement is JsonObject && jsonElement.containsKey("data")) {
                    val objects = jsonElement["data"]?.jsonObject?.get("objects")?.jsonArray
                    if (objects != null && objects.isNotEmpty()) {
                        val firstObj = objects[0].jsonObject
                        val names = firstObj["names"]?.jsonObject
                        val officialName = names?.get("common")?.jsonPrimitive?.content ?: trimmed

                        val capitals = firstObj["capitals"]?.jsonArray
                        val capital = capitals?.getOrNull(0)?.jsonObject?.get("name")?.jsonPrimitive?.content ?: "N/A"

                        val population = firstObj["population"]?.jsonPrimitive?.long ?: 0L

                        val currencies = firstObj["currencies"]?.jsonArray
                        val firstCurr = currencies?.getOrNull(0)?.jsonObject
                        val currencyCode = firstCurr?.get("code")?.jsonPrimitive?.content ?: "USD"
                        val currencyName = firstCurr?.get("name")?.jsonPrimitive?.content ?: "United States Dollar"

                        return Country(
                            name = officialName,
                            capital = capital,
                            population = population,
                            currencyCode = currencyCode,
                            currencyName = currencyName
                        )
                    }
                }

                // Format REST Countries v3.1 (Array root): [ { ... } ]
                if (jsonElement is JsonArray && jsonElement.isNotEmpty()) {
                    val firstItem = jsonElement[0].jsonObject
                    val officialName = firstItem["name"]?.jsonObject?.get("common")?.jsonPrimitive?.content ?: trimmed
                    val capital = firstItem["capital"]?.jsonArray?.get(0)?.jsonPrimitive?.content ?: "N/A"
                    val population = firstItem["population"]?.jsonPrimitive?.long ?: 0L

                    val currenciesObj = firstItem["currencies"]?.jsonObject
                    val currencyCode = currenciesObj?.keys?.firstOrNull() ?: "USD"
                    val currencyName = currenciesObj?.get(currencyCode)?.jsonObject?.get("name")?.jsonPrimitive?.content ?: "United States Dollar"

                    return Country(
                        name = officialName,
                        capital = capital,
                        population = population,
                        currencyCode = currencyCode,
                        currencyName = currencyName
                    )
                }
            }
        } catch (_: Exception) {
            // Lanjut ke fallback jika REST Countries v5 mengalami kendala
        }

        // 2. Fallback: CountriesNow API
        try {
            val encodedName = java.net.URLEncoder.encode(trimmed, "UTF-8")
            val capitalRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://countriesnow.space/api/v0.1/countries/capital/q?country=$encodedName"))
                .header("User-Agent", "TravelWise/1.0")
                .timeout(Duration.ofSeconds(4))
                .GET()
                .build()

            val capitalResponse = client.send(capitalRequest, HttpResponse.BodyHandlers.ofString())
            if (capitalResponse.statusCode() == 200) {
                val jsonObj = Json.parseToJsonElement(capitalResponse.body()).jsonObject
                if (jsonObj["error"]?.jsonPrimitive?.boolean == false) {
                    val data = jsonObj["data"]?.jsonObject
                    val countryName = data?.get("name")?.jsonPrimitive?.content ?: trimmed
                    val capital = data?.get("capital")?.jsonPrimitive?.content ?: "N/A"

                    var currencyCode = "USD"
                    try {
                        val currRequest = HttpRequest.newBuilder()
                            .uri(URI.create("https://countriesnow.space/api/v0.1/countries/currency/q?country=$encodedName"))
                            .timeout(Duration.ofSeconds(3))
                            .GET()
                            .build()
                        val currResponse = client.send(currRequest, HttpResponse.BodyHandlers.ofString())
                        if (currResponse.statusCode() == 200) {
                            val currJson = Json.parseToJsonElement(currResponse.body()).jsonObject
                            currencyCode = currJson["data"]?.jsonObject?.get("currency")?.jsonPrimitive?.content ?: "USD"
                        }
                    } catch (_: Exception) {}

                    return Country(
                        name = countryName,
                        capital = capital,
                        population = defaultCountries[trimmed.lowercase()]?.population ?: 10000000L,
                        currencyCode = currencyCode,
                        currencyName = currencyCode
                    )
                }
            }
        } catch (_: Exception) {
            // Lanjut ke default map
        }

        // 3. Fallback: Kamus Lokal (Offline Safe)
        val lookup = defaultCountries[trimmed.lowercase()]
        if (lookup != null) {
            return lookup
        }

        // 4. Fallback umum: anggap nama input sebagai kota/tujuan
        return Country(
            name = trimmed.replaceFirstChar { it.uppercase() },
            capital = trimmed.replaceFirstChar { it.uppercase() },
            population = 5000000L,
            currencyCode = "USD",
            currencyName = "USD"
        )
    }
}