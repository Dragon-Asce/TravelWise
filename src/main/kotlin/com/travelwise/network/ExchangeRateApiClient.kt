package com.travelwise.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.*

/**
 * ExchangeRate API (open.er-api.com). fetchData("USD") -> peta kode mata uang ke kurs.
 * Hasil di-cache 1 jam agar tidak membebani API.
 */
class ExchangeRateApiClient(
    private val baseUrl: String = "https://open.er-api.com/v6",
    private val client: HttpClient = HttpClients.default
) : ApiClient<Map<String, Double>> {

    private data class Cached(val rates: Map<String, Double>, val at: Long)

    private val cache = ConcurrentHashMap<String, Cached>()
    private val ttlMillis = 60 * 60 * 1000L

    override suspend fun fetchData(query: String): Map<String, Double>? {
        val base = query.trim().uppercase()
        if (base.isEmpty()) return null

        val now = System.currentTimeMillis()
        cache[base]?.let { if (now - it.at < ttlMillis) return it.rates }

        return try {
            val text = client.get("$baseUrl/latest/$base").bodyAsText()
            val root = Json.parseToJsonElement(text).jsonObject
            if (root["result"]?.jsonPrimitive?.contentOrNull != "success") return null

            val rates = root["rates"]?.jsonObject
                ?.mapNotNull { (code, v) -> v.jsonPrimitive.doubleOrNull?.let { code to it } }
                ?.toMap()
                ?: return null

            cache[base] = Cached(rates, now)
            rates
        } catch (e: Exception) {
            println("[ExchangeRateApiClient] Gagal mengambil kurs $base: ${e.message}")
            null
        }
    }

    suspend fun getExchangeRate(baseCurrency: String, targetCurrency: String): Double? =
        fetchData(baseCurrency)?.get(targetCurrency.uppercase())
}