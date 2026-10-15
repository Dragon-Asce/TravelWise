package com.travelwise.network

import com.travelwise.model.Place
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * Klien HTTP untuk mengakses OpenStreetMap Overpass API (`https://overpass-api.de/api/interpreter`).
 *
 * Mengimplementasikan antarmuka [ApiClient] bertipe `List<Place>` guna mencari lokasi wisata
 * nyata (*Points of Interest* / POI) di sekitar koordinat geografis kota destinasi.
 *
 * Seluruh kategori lokasi (museum, galeri, kafe, atraksi wisata, taman terbuka, situs bersejarah)
 * ditarik dalam satu query batch Overpass QL berkinerja tinggi, lalu dipilah secara polimorfis
 * oleh modul rekomendasi aktivitas ([com.travelwise.service.ActivityRecommender]) sesuai kondisi cuaca.
 *
 * @property baseUrl Alamat endpoint Overpass interpreter (default: `https://overpass-api.de/api/interpreter`).
 * @property client Instance [HttpClient] bersama untuk eksekusi panggilan HTTP POST.
 * @property radiusMeters Radius pemindaian lokasi wisata dalam satuan meter dari titik koordinat pusat (default: 10.000 meter / 10 km).
 * @author Farhan
 */
class OverpassApiClient(
    private val baseUrl: String = "https://overpass-api.de/api/interpreter",
    private val client: HttpClient = HttpClients.default,
    private val radiusMeters: Int = 10_000
) : ApiClient<List<Place>> {

    /**
     * Mengambil daftar tempat wisata berdasarkan string gabungan koordinat lintang dan bujur.
     *
     * @param query String koordinat dengan format `"lat,lon"` (contoh: `"-6.9175,107.6191"`).
     * @return Daftar objek [Place] yang ditemukan, atau `null` jika format query tidak valid.
     */
    override suspend fun fetchData(query: String): List<Place>? {
        val parts = query.split(",")
        val lat = parts.getOrNull(0)?.trim()?.toDoubleOrNull() ?: return null
        val lon = parts.getOrNull(1)?.trim()?.toDoubleOrNull() ?: return null
        return fetchPlaces(lat, lon)
    }

    /**
     * Menjalankan kueri Overpass QL untuk mengekstrak tempat wisata nyata di sekitar koordinat kota.
     *
     * Kueri mencakup entitas Node, Way, dan Relation (nwr) untuk kategori:
     * - `tourism=museum` (Indoor)
     * - `tourism=gallery` (Indoor)
     * - `amenity=cafe` (Indoor)
     * - `tourism=attraction` (Outdoor/Campuran)
     * - `leisure=park` (Outdoor)
     * - `historic=*` (Outdoor/Campuran)
     *
     * @param lat Koordinat garis lintang (*latitude*) kota.
     * @param lon Koordinat garis bujur (*longitude*) kota.
     * @return Daftar unik objek [Place] yang berhasil diekstrak dan dideduplikasi berdasarkan nama tempat.
     */
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

    /**
     * Fungsi utilitas ekstensi untuk mengambil nilai teks secara aman dari [JsonObject].
     *
     * @param key Kunci properti JSON yang ingin dibaca.
     * @return String konten properti jika ada, atau `null` jika tidak ditemukan/bernilai null.
     */
    private fun JsonObject.str(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    /**
     * Mem-parsing elemen tunggal respons Overpass JSON menjadi objek domain model [Place].
     *
     * Memprioritaskan penamaan internasional (`name:en`) sebelum nama lokal (`name`)
     * dan mengekstrak koordinat titik tengah (*center latitude/longitude*).
     *
     * @param el Objek JSON yang merepresentasikan satu entitas OpenStreetMap.
     * @return Entitas [Place] yang valid, atau `null` jika elemen tidak memiliki nama atau kategori yang sesuai.
     */
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