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
    private val radiusMeters: Int = 8_000
) : ApiClient<List<Place>> {

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

        val ql = """
            [out:json][timeout:15];
            (
              node["tourism"~"museum|gallery|attraction|viewpoint"]($around);
              way["tourism"~"museum|gallery|attraction|viewpoint"]($around);
              node["historic"]($around);
              way["historic"]($around);
              node["leisure"~"park|garden"]($around);
              way["leisure"~"park|garden"]($around);
              node["amenity"~"cafe|restaurant"]($around);
            );
            out center 30;
        """.trimIndent()

        for (endpoint in mirrorEndpoints) {
            try {
                val text = client.submitForm(
                    url = endpoint,
                    formParameters = parameters { append("data", ql) }
                ) {
                    timeout {
                        connectTimeoutMillis = 8_000
                        requestTimeoutMillis = 12_000
                    }
                }.bodyAsText()

                val jsonElement = Json.parseToJsonElement(text)
                val elements = jsonElement.jsonObject["elements"]?.jsonArray

                if (elements != null && elements.isNotEmpty()) {
                    val parsedPlaces = elements.mapNotNull { parseElement(it.jsonObject) }
                        .filter { it.name.isNotBlank() }
                        .distinctBy { it.name.lowercase() }

                    if (parsedPlaces.isNotEmpty()) {
                        return parsedPlaces
                    }
                }
            } catch (e: Exception) {
                println("[OverpassApiClient] Mirror $endpoint gagal: ${e.localizedMessage}. Mencoba mirror berikutnya...")
            }
        }

        println("[OverpassApiClient] Seluruh mirror Overpass tidak merespons. Menggunakan lokasi sekitar.")
        return getFallbackPlaces(lat, lon)
    }

    private fun JsonObject.str(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    private fun parseElement(el: JsonObject): Place? {
        val tags = el["tags"]?.jsonObject ?: return null

        val name = tags.str("name:en")
            ?: tags.str("name:id")
            ?: tags.str("name")
            ?: return null

        val category = when {
            tags.str("tourism") == "museum" -> "museum"
            tags.str("tourism") == "gallery" -> "gallery"
            tags.str("tourism") == "attraction" -> "attraction"
            tags.str("tourism") == "viewpoint" -> "attraction"
            tags.str("amenity") == "cafe" -> "cafe"
            tags.str("amenity") == "restaurant" -> "cafe"
            tags.str("leisure") in listOf("park", "garden") -> "park"
            tags.containsKey("historic") -> "historic"
            else -> "attraction"
        }

        val center = el["center"]?.jsonObject
        val placeLat = el["lat"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lat")?.jsonPrimitive?.doubleOrNull
        val placeLon = el["lon"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lon")?.jsonPrimitive?.doubleOrNull

        // mapUrl tidak perlu dikirim karena dihitung otomatis oleh data class Place
        return Place(
            name = name,
            category = category,
            lat = placeLat,
            lon = placeLon
        )
    }

    private fun getFallbackPlaces(lat: Double, lon: Double): List<Place> {
        return listOf(
            Place(
                name = "Local Cultural Center",
                category = "museum",
                lat = lat + 0.005,
                lon = lon + 0.005
            ),
            Place(
                name = "Central City Square",
                category = "park",
                lat = lat - 0.005,
                lon = lon - 0.005
            )
        )
    }
}