package com.travelwise.network

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URI
import kotlinx.serialization.json.*

class ExchangeRateApiClient(
    private val baseUrl: String = "https://open.er-api.com/v6"
) {
    private val client = HttpClient.newHttpClient()

    // Mengambil kurs mata uang (misal: IDR ke USD / EUR)
    fun getExchangeRate(baseCurrency: String, targetCurrency: String): Double? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$baseUrl/latest/$baseCurrency"))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) return null

            val jsonObject = Json.parseToJsonElement(response.body()).jsonObject
            val rates = jsonObject["rates"]?.jsonObject

            rates?.get(targetCurrency)?.jsonPrimitive?.double
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}