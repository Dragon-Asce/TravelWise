package com.travelwise.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.*

/**
 * Klien HTTP untuk berinteraksi dengan Unsplash API (`https://api.unsplash.com/search/photos`).
 *
 * Mengimplementasikan antarmuka [ApiClient] bertipe [String] guna mencari dan menyediakan
 * URL foto lanskap beresolusi tinggi untuk destinasi utama maupun tempat-tempat wisata rekomendasi.
 *
 * Fitur keandalan & efisiensi:
 * - **In-Memory Caching**: Menyimpan tautan foto yang sudah pernah dicari ke dalam [ConcurrentHashMap]
 *   agar menghemat kuota *rate limit* Unsplash API.
 * - **Graceful Degradation (Fallback SVG Generator)**: Jika kunci API kosong, kuota habis (*HTTP 403/429*),
 *   atau foto tidak ditemukan, sistem secara otomatis menghasilkan gambar SVG gradien dinamis berbasis Data URI
 *   sehingga antarmuka pengguna tidak pernah menampilkan gambar rusak (*broken image*).
 *
 * @property accessKey Kunci akses aplikasi Unsplash (Client-ID) yang dibaca dari variabel lingkungan `UNSPLASH_API_KEY`.
 * @property client Instance [HttpClient] bersama untuk eksekusi panggilan HTTP.
 * @author Farhan
 */
class UnsplashApiClient(
    private val accessKey: String = System.getenv("UNSPLASH_API_KEY") ?: "poZD4zROka_9HjAXdLC3sZnmBDs938bcXzOF1sKAeFU",
    private val client: HttpClient = HttpClients.default
) : ApiClient<String> {

    private val baseUrl = "https://api.unsplash.com/search/photos"
    private val cache = ConcurrentHashMap<String, String>()

    /**
     * Mengambil URL gambar berdasarkan kata kunci pencarian (implementasi [ApiClient]).
     *
     * @param query Kata kunci foto yang dicari (misal: "Tokyo travel").
     * @return URL gambar dari Unsplash atau Data URI SVG placeholder.
     */
    override suspend fun fetchData(query: String): String? = fetchImage(query)

    /**
     * Mencari URL foto dengan dukungan bertingkat (*hierarchical fallback*).
     *
     * Alur eksekusi:
     * 1. Memeriksa memori cache lokal.
     * 2. Mencari dengan kata kunci utama [query].
     * 3. Jika tidak ditemukan dan [fallbackQuery] tersedia, mencoba mencari dengan [fallbackQuery].
     * 4. Jika tetap tidak ditemukan atau terjadi kendala jaringan, menghasilkan gambar placeholder SVG.
     *
     * @param query Kata kunci pencarian spesifik (misal: "Tokyo Skytree Tokyo").
     * @param fallbackQuery Kata kunci cadangan yang lebih umum (misal: "attraction Tokyo" atau "Japan").
     * @return Tautan string URL foto yang siap ditampilkan pada tag `<img>`.
     */
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

    /**
     * Menjalankan kueri pencarian langsung ke endpoint HTTP Unsplash API.
     *
     * Mengambil foto berorientasi lanskap (*landscape*) dan mengekstrak properti `urls.small`.
     *
     * @param query Parameter teks pencarian foto.
     * @return Tautan URL foto ukuran kecil jika ada, atau `null` jika tidak ditemukan/error.
     */
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

    /**
     * Menghasilkan gambar placeholder SVG berbasis teks dengan palet warna gradien deterministik.
     *
     * Palet warna dipilih berdasarkan nilai *hash code* dari [label], sehingga kata kunci yang sama
     * akan selalu menghasilkan warna gradien yang konsisten.
     *
     * @param label Teks keterangan yang akan dicantumkan di bagian tengah gambar SVG.
     * @return String berformat `data:image/svg+xml;utf8,...` yang dapat langsung disematkan pada atribut `src`.
     */
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