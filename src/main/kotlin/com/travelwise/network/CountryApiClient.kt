package com.travelwise.network

import com.travelwise.model.Country
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import kotlinx.serialization.json.*

class CountryApiClient(
    private val baseUrl: String = "https://restcountries.com/v3.1"
) {
    private val client = HttpClient.newHttpClient()

    fun fetchCountry(name: String): Country? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseUrl/name/$name"))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) return null

            // Parsing JSON manual/otomatis dari response REST Countries
            val jsonArray = Json.parseToJsonElement(response.body()).jsonArray
            val firstItem = jsonArray[0].jsonObject

            val officialName = firstItem["name"]?.jsonObject?.get("common")?.jsonPrimitive?.content ?: name
            val capital = firstItem["capital"]?.jsonArray?.get(0)?.jsonPrimitive?.content ?: "N/A"
            val population = firstItem["population"]?.jsonPrimitive?.long ?: 0L

            Country(
                name = officialName,
                capital = capital,
                population = population
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}