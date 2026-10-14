package com.travelwise.network

import com.travelwise.model.Place
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * Overpass API (OpenStreetMap): mencari POI nyata di sekitar koordinat kota.
 * Satu query mengambil semua kategori (indoor & outdoor); pemilahan dilakukan
 * oleh ActivityRecommender sesuai cuaca.
 */
class OverpassApiClient(
    private val baseUrl: String = "https://overpass-api.de/api/interpreter",
    private val client: HttpClient = HttpClients.default,
    private val radiusMeters: Int = 10_000
) : ApiClient<List<Place>> {

    /** @param query format "lat,lon" */
    override suspend fun fetchData(query: String): List<Place>? {
        val parts = query.split(",")
        val lat = parts.getOrNull(0)?.trim()?.toDoubleOrNull() ?: return null
        val lon = parts.getOrNull(1)?.trim()?.toDoubleOrNull() ?: return null
        return fetchPlaces(lat, lon)
    }

    suspend fun fetchPlaces(lat: Double, lon: Double): List<Place> {
        val around = "around:$radiusMeters,$lat,$lon"
        val ql = """
            [out:json][timeout:20];
            nwr["tourism"="museum"]["name"]($around);
            out center 12;
            nwr["tourism"="gallery"]["name"]($around);
            out center 8;
            nwr["amenity"="cafe"]["name"]($around);
            out center 12;
            nwr["tourism"="attraction"]["name"]($around);
            out center 15;
            nwr["leisure"="park"]["name"]($around);
            out center 12;
            nwr["historic"]["name"]($around);
            out center 12;
        """.trimIndent()

        return try {
            val text = client.submitForm(
                url = baseUrl,
                formParameters = parameters { append("data", ql) }
            ) {
                timeout { requestTimeoutMillis = 25_000 }
            }.bodyAsText()

            val elements = Json.parseToJsonElement(text).jsonObject["elements"]?.jsonArray
                ?: return emptyList()

            elements.mapNotNull { parseElement(it.jsonObject) }
                .distinctBy { it.name.lowercase() }
        } catch (e: Exception) {
            println("[OverpassApiClient] Gagal mengambil tempat: ${e.message}")
            emptyList()
        }
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
            else -> return null
        }

        val center = el["center"]?.jsonObject
        val lat = el["lat"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lat")?.jsonPrimitive?.doubleOrNull
        val lon = el["lon"]?.jsonPrimitive?.doubleOrNull ?: center?.get("lon")?.jsonPrimitive?.doubleOrNull

        return Place(name = name, category = category, lat = lat, lon = lon)
    }
}