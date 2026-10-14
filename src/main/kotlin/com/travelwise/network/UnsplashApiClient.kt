package com.travelwise.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.*

/**
 * Unsplash API untuk foto destinasi & tempat wisata.
 * Jika key kosong / limit habis / tidak ada hasil -> placeholder gradient SVG (data URI),
 * karena source.unsplash.com sudah dihentikan.
 */
class UnsplashApiClient(
    private val accessKey: String = System.getenv("UNSPLASH_API_KEY") ?: "",
    private val client: HttpClient = HttpClients.default
) : ApiClient<String> {

    private val baseUrl = "https://api.unsplash.com/search/photos"
    private val cache = ConcurrentHashMap<String, String>()

    override suspend fun fetchData(query: String): String? = fetchImage(query)

    /** Coba [query]; jika kosong coba [fallbackQuery]; jika gagal -> placeholder. */
    suspend fun fetchImage(query: String, fallbackQuery: String? = null): String {
        val key = query.trim().lowercase()
        cache[key]?.let { return it }

        val found = search(query) ?: fallbackQuery?.let { search(it) }
        if (found != null) {
            cache[key] = found
            return found
        }
        return placeholder(fallbackQuery ?: query)
    }

    private suspend fun search(query: String): String? {
        if (accessKey.isBlank()) return null
        return try {
            val text = client.get(baseUrl) {
                header("Authorization", "Client-ID $accessKey")
                parameter("query", query)
                parameter("per_page", 1)
                parameter("orientation", "landscape")
            }.bodyAsText()

            Json.parseToJsonElement(text).jsonObject["results"]?.jsonArray
                ?.firstOrNull()?.jsonObject?.get("urls")?.jsonObject
                ?.get("small")?.jsonPrimitive?.contentOrNull
        } catch (e: Exception) {
            println("[UnsplashApiClient] Gagal untuk '$query': ${e.message}")
            null
        }
    }

    private fun placeholder(label: String): String {
        val palettes = listOf(
            "#0ea5e9" to "#6366f1", "#10b981" to "#0ea5e9",
            "#f59e0b" to "#ef4444", "#8b5cf6" to "#ec4899"
        )
        val (c1, c2) = palettes[Math.floorMod(label.hashCode(), palettes.size)]
        val safe = label.take(28).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        val svg = "<svg xmlns='http://www.w3.org/2000/svg' width='600' height='400'>" +
            "<defs><linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>" +
            "<stop offset='0' stop-color='$c1'/><stop offset='1' stop-color='$c2'/></linearGradient></defs>" +
            "<rect width='600' height='400' fill='url(#g)'/>" +
            "<text x='50%' y='50%' fill='white' fill-opacity='0.85' font-family='sans-serif' font-size='28' " +
            "text-anchor='middle' dominant-baseline='middle'>$safe</text></svg>"
        return "data:image/svg+xml;utf8," + URLEncoder.encode(svg, StandardCharsets.UTF_8).replace("+", "%20")
    }
}