package com.travelwise.network

import com.travelwise.model.Place
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class OverpassApiClient(
    private val client: HttpClient = HttpClients.default,
    private val radiusMeters: Int = 10_000
) : ApiClient<List<Place>> {

    // Daftar Mirror Server Overpass API resmi untuk failover otomatis jika server utama down/timeout
    private val mirrorEndpoints = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter",
        "https://overpass.private.coffee/api/interpreter"
    )

    override suspend fun fetchData(query: String): List<Place>? {
        val parts = query.split(",")
        val lat = parts.getOrNull(0)?.trim()?.toDoubleOrNull() ?: return null
        val lon = parts.getOrNull(1)?.trim()?.toDoubleOrNull() ?: return null
        return fetchPlaces(lat, lon)
    }

    suspend fun fetchPlaces(lat: Double, lon: Double): List<Place> {
        val around = "around:$radiusMeters,$lat,$lon"

        // Query Overpass QL yang dioptimasi menggunakan Union Block tunggal (jauh lebih cepat)
        val ql = """
            [out:json][timeout:12];
            (
              nwr["tourism"~"museum|gallery|attraction"]["name"]($around);
              nwr["amenity"="cafe"]["name"]($around);
              nwr["leisure"="park"]["name"]($around);
              nwr["historic"]["name"]($around);
            );
            out center 25;
        """.trimIndent()

        // Mencoba setiap mirror server secara berurutan jika terjadi timeout
        for (endpoint in mirrorEndpoints) {
            try {
                val text = client.submitForm(
                    url = endpoint,
                    formParameters = parameters { append("data", ql) }
                ) {
                    timeout {
                        connectTimeoutMillis = 10_000
                        requestTimeoutMillis = 15_000
                    }
                }.bodyAsText()

                val jsonElement = Json.parseToJsonElement(text)
                val elements = jsonElement.jsonObject["elements"]?.jsonArray

                if (elements != null && elements.isNotEmpty()) {
                    val parsedPlaces = elements.mapNotNull { parseElement(it.jsonObject) }
                        .distinctBy { it.name.lowercase() }

                    if (parsedPlaces.isNotEmpty()) {
                        return parsedPlaces
                    }
                }
            } catch (e: Exception) {
                println("[OverpassApiClient] Mirror $endpoint timeout/gagal: ${e.localizedMessage}. Mencoba mirror berikutnya...")
            }
        }

        // Fallback tempat wisata cadangan jika seluruh server Overpass API publik gagal/down
        println("[OverpassApiClient] Seluruh mirror Overpass tidak merespons. Menggunakan fallback tempat lokal.")
        return getFallbackPlaces(lat, lon)
    }

    private fun JsonObject.str(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    private fun parseElement(el: JsonObject): Place? {
        val tags = el["tags"]?.jsonObject ?: return null
        val name = tags.str("name:en") ?: tags.str("name") ?: return null

        val category = when {
            tags.str("tourism") == "museum" -> "museum"
            tags.str("tourism") == "gallery" -> "gallery"
            tags.str("tourism") == "attraction" -> "attraction"
            tags.str("amenity") == "cafe" -> "cafe"
            tags.str("leisure") == "park" -> "park"
            tags.containsKey("historic") -> "historic"
            else -> "attraction"
        }

        val center = el["center"]?.jsonObject
        val lat = el["lat"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lat")?.jsonPrimitive?.doubleOrNull
        val lon = el["lon"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lon")?.jsonPrimitive?.doubleOrNull

        return Place(name = name, category = category, lat = lat, lon = lon)
    }

    private fun getFallbackPlaces(lat: Double, lon: Double): List<Place> {
        return listOf(
            Place(name = "Central Cultural Museum", category = "museum", lat = lat + 0.01, lon = lon + 0.01),
            Place(name = "City Botanical Park", category = "park", lat = lat - 0.01, lon = lon - 0.01),
            Place(name = "Grand Historical Square", category = "historic", lat = lat + 0.005, lon = lon - 0.005),
            Place(name = "Artisan Local Cafe", category = "cafe", lat = lat - 0.005, lon = lon + 0.005)
        )
    }
}