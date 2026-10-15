package com.travelwise.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.util.concurrent.ConcurrentHashMap
import kotlinx.serialization.json.*

/**
 * Klien HTTP untuk mengambil data nilai tukar mata uang asing (*exchange rates*) terkini.
 *
 * Menggunakan layanan publik open.er-api.com untuk menyediakan konversi valuta asing
 * yang akurat dalam modul kalkulasi estimasi anggaran perjalanan ([com.travelwise.model.BudgetPlan]).
 *
 * Fitur optimasi kinerja:
 * - **In-Memory Caching**: Menyimpan respons kurs dalam [ConcurrentHashMap] dengan batas kedaluwarsa (*Time-To-Live*) 1 jam.
 * - **Thread-Safe**: Aman diakses secara konkuren oleh banyak coroutine tanpa *race condition*.
 *
 * @property baseUrl Alamat basis API penyedia kurs valuta asing (default: `https://open.er-api.com/v6`).
 * @property client Klien HTTP Ktor bersama yang digunakan untuk eksekusi panggilan jaringan.
 * @author Farhan
 */
class ExchangeRateApiClient(
    private val baseUrl: String = "https://open.er-api.com/v6",
    private val client: HttpClient = HttpClients.default
) : ApiClient<Map<String, Double>> {

    /**
     * Struktur data pembungkus cache internal yang menyimpan peta kurs beserta stempel waktu pembuatannya.
     *
     * @property rates Peta pasangan kode mata uang tujuan ke nilai kursnya.
     * @property at Waktu dalam milidetik (Epoch time) saat cache disimpan.
     */
    private data class Cached(val rates: Map<String, Double>, val at: Long)

    private val cache = ConcurrentHashMap<String, Cached>()
    private val ttlMillis = 60 * 60 * 1000L

    /**
     * Mengambil daftar kurs mata uang lengkap berdasarkan kode mata uang acuan/basis.
     *
     * Jika data untuk [query] sudah tersimpan di cache dan belum kedaluwarsa (kurang dari 1 jam),
     * maka data dari memori cache akan langsung dikembalikan tanpa melakukan panggilan jaringan baru.
     *
     * @param query Kode mata uang basis tiga huruf (contoh: "USD", "EUR").
     * @return [Map] berisi kode mata uang tujuan beserta nilai konversinya, atau `null` jika terjadi kegagalan.
     */
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

    /**
     * Mengambil nilai konversi spesifik antara satu mata uang basis ke satu mata uang target.
     *
     * Contoh: `getExchangeRate("USD", "IDR")` menghasilkan nilai kurs 1 USD dalam rupiah (misal: 15700.0).
     *
     * @param baseCurrency Kode mata uang asal (misal: "USD").
     * @param targetCurrency Kode mata uang tujuan yang dicari (misal: "IDR", "JPY").
     * @return Angka penggali nilai tukar bertipe [Double], atau `null` jika tidak ditemukan/gagal.
     */
    suspend fun getExchangeRate(baseCurrency: String, targetCurrency: String): Double? =
        fetchData(baseCurrency)?.get(targetCurrency.uppercase())
}