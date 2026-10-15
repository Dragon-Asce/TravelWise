package com.travelwise.network

import com.travelwise.model.Country
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Data Transfer Object (DTO) untuk membungkus struktur terluar dari respons REST Countries API v5.
 *
 * @property data Kontainer data utama yang menyimpan koleksi objek negara.
 * @author Farhan
 */
@Serializable
data class RestCountriesResponse(
    @SerialName("data") val data: RestCountriesData? = null
)

/**
 * Data Transfer Object (DTO) penampung daftar objek data negara.
 *
 * @property objects Koleksi data negara bertipe [CountryDto].
 */
@Serializable
data class RestCountriesData(
    @SerialName("objects") val objects: List<CountryDto> = emptyList()
)

/**
 * Data Transfer Object (DTO) representasi satu entitas profil negara dari REST Countries API v5.
 *
 * @property names Informasi variasi nama negara (nama umum dan nama resmi).
 * @property capitals Daftar ibukota yang dimiliki oleh negara terkait.
 * @property population Estimasi total populasi penduduk terkini.
 * @property currencies Daftar mata uang resmi yang berlaku di negara tersebut.
 * @property flag Informasi aset gambar lambang atau bendera nasional.
 * @property codes Informasi kode identifikasi ISO (Alpha-2 dan Alpha-3).
 */
@Serializable
data class CountryDto(
    @SerialName("names") val names: NamesDto? = null,
    @SerialName("capitals") val capitals: List<CapitalDto> = emptyList(),
    @SerialName("population") val population: Long? = 0,
    @SerialName("currencies") val currencies: List<CurrencyDto> = emptyList(),
    @SerialName("flag") val flag: FlagDto? = null,
    @SerialName("codes") val codes: CodesDto? = null
)

/**
 * Data Transfer Object (DTO) untuk nama umum dan nama resmi negara.
 *
 * @property common Nama umum yang biasa digunakan dalam percakapan sehari-hari.
 * @property official Nama diplomatik atau nama resmi kenegaraan.
 */
@Serializable
data class NamesDto(
    @SerialName("common") val common: String = "",
    @SerialName("official") val official: String = ""
)

/**
 * Data Transfer Object (DTO) untuk detail ibukota negara.
 *
 * @property name Nama kota yang menjadi pusat administrasi/ibukota.
 */
@Serializable
data class CapitalDto(
    @SerialName("name") val name: String = ""
)

/**
 * Data Transfer Object (DTO) representasi mata uang resmi negara.
 *
 * @property code Kode valuta standar internasional ISO 4217 (contoh: "JPY", "IDR", "USD").
 * @property name Nama lengkap dari mata uang terkait (contoh: "Indonesian Rupiah").
 * @property symbol Simbol penulisan mata uang (contoh: "Rp", "$", "¥").
 */
@Serializable
data class CurrencyDto(
    @SerialName("code") val code: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("symbol") val symbol: String = ""
)

/**
 * Data Transfer Object (DTO) untuk tautan gambar bendera negara.
 *
 * @property urlPng URL gambar bendera dalam format raster PNG.
 * @property urlSvg URL gambar bendera dalam format vektor SVG.
 */
@Serializable
data class FlagDto(
    @SerialName("url_png") val urlPng: String? = null,
    @SerialName("url_svg") val urlSvg: String? = null
)

/**
 * Data Transfer Object (DTO) untuk kode ISO negara.
 *
 * @property alpha2 Kode negara dua huruf standar ISO 3166-1 alpha-2 (contoh: "ID", "JP").
 * @property alpha3 Kode negara tiga huruf standar ISO 3166-1 alpha-3 (contoh: "IDN", "JPN").
 */
@Serializable
data class CodesDto(
    @SerialName("alpha_2") val alpha2: String = "",
    @SerialName("alpha_3") val alpha3: String = ""
)

/**
 * Klien HTTP untuk berinteraksi dengan REST Countries API v5 (`https://api.restcountries.com/countries/v5`).
 *
 * Kelas ini mengimplementasikan antarmuka [ApiClient] bertipe [Country] dan bertugas
 * mengambil profil geografis, demografis, dan moneter dari suatu negara.
 *
 * Fitur utama:
 * - **Autentikasi Bearer Token**: Mengirimkan kunci API melalui header HTTP Authorization.
 * - **Translasi Alias Lokal**: Mendukung pencarian menggunakan nama bahasa Indonesia (misal: "Jepang" -> "Japan").
 * - **Lookup Fleksibel**: Mendukung pencarian teks bebas (nama negara / ibukota) dan pencarian via kode ISO Alpha-2.
 * - **Offline Fallback Map**: Menyediakan data cadangan untuk negara-negara populer apabila kuota API habis atau server offline.
 *
 * @property apiKey Kunci otentikasi REST Countries API yang dibaca dari variabel lingkungan `RESTCOUNTRIES_API_KEY`.
 * @property client Instance [HttpClient] yang digunakan untuk mengeksekusi request HTTP.
 * @author Farhan
 */
class CountryApiClient(
    private val apiKey: String = System.getenv("RESTCOUNTRIES_API_KEY")
        ?: "rc_live_dab3068ca449486a8d6b4bb00af2acb2",
    private val client: HttpClient = HttpClients.default
) : ApiClient<Country> {

    private companion object {
        const val BASE_URL = "https://api.restcountries.com/countries/v5"

        /**
         * Pemetaan istilah alias nama negara dalam bahasa Indonesia ke nama kanonik bahasa Inggris.
         */
        val ALIASES = mapOf(
            "jepang" to "Japan",
            "inggris" to "United Kingdom",
            "uk" to "United Kingdom",
            "amerika" to "United States",
            "amerika serikat" to "United States",
            "usa" to "United States",
            "jerman" to "Germany",
            "prancis" to "France",
            "perancis" to "France",
            "belanda" to "Netherlands",
            "tiongkok" to "China",
            "cina" to "China",
            "korea" to "South Korea",
            "korea selatan" to "South Korea",
            "rusia" to "Russia",
            "australia" to "Australia",
            "singapura" to "Singapore",
            "malaysia" to "Malaysia",
            "spanyol" to "Spain",
            "italia" to "Italy",
            "swis" to "Switzerland",
            "swiss" to "Switzerland",
            "arab saudi" to "Saudi Arabia"
        )

        /**
         * Kamus data negara statis sebagai fallback darurat saat jaringan tidak tersedia.
         */
        val FALLBACK_COUNTRIES = listOf(
            Country("Indonesia", "Jakarta", 273800000L, "IDR", "Indonesian Rupiah"),
            Country("Japan", "Tokyo", 125800000L, "JPY", "Japanese Yen"),
            Country("United States", "Washington, D.C.", 331900000L, "USD", "United States Dollar"),
            Country("United Kingdom", "London", 67081000L, "GBP", "British Pound"),
            Country("France", "Paris", 67750000L, "EUR", "Euro"),
            Country("Germany", "Berlin", 83200000L, "EUR", "Euro"),
            Country("Switzerland", "Bern", 8700000L, "CHF", "Swiss Franc"),
            Country("Singapore", "Singapore", 5450000L, "SGD", "Singapore Dollar"),
            Country("Australia", "Canberra", 25690000L, "AUD", "Australian Dollar"),
            Country("Italy", "Rome", 59000000L, "EUR", "Euro")
        )
    }

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Mengambil data profil negara berdasarkan kata kunci pencarian umum (implementasi [ApiClient]).
     *
     * @param query Nama negara atau kata kunci pencarian.
     * @return Objek [Country] jika ditemukan, atau `null` jika tidak ada hasil.
     */
    override suspend fun fetchData(query: String): Country? = fetchCountry(query)

    /**
     * Mencari data negara secara komprehensif menggunakan API atau kamus lokal fallback.
     *
     * Alur proses:
     * 1. Melakukan normalisasi spasi dan pengecekan apakah query kosong.
     * 2. Menerjemahkan alias bahasa Indonesia jika tersedia.
     * 3. Memanggil endpoint pencarian REST Countries v5 `?q=...`.
     * 4. Jika tidak ada hasil dari API eksternal, memeriksa kecocokan di kamus fallback lokal.
     *
     * @param query Kata kunci nama negara (misal: "Indonesia", "jepang", "Japan").
     * @return Entitas domain [Country] yang telah dipetakan, atau `null` jika gagal.
     */
    suspend fun fetchCountry(query: String): Country? {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) return null

        val mappedQuery = ALIASES[cleanQuery.lowercase()] ?: cleanQuery

        // 1. Coba REST Countries v5 search endpoint (?q=...)
        val searchResult = fetchFromUrl("$BASE_URL?q=${mappedQuery.encodeURLParameter()}")
        if (searchResult != null) return searchResult

        // 2. Offline fallback
        return FALLBACK_COUNTRIES.firstOrNull {
            it.name.equals(mappedQuery, ignoreCase = true) ||
            it.capital.equals(mappedQuery, ignoreCase = true)
        }
    }

    /**
     * Mengambil data profil negara berdasarkan 2 huruf kode ISO 3166-1 alpha-2.
     *
     * Digunakan terutama dalam mekanisme *smart city reverse lookup* (contoh: cuaca Bandung -> negara "ID" -> Indonesia).
     *
     * @param code Kode negara dua huruf (misal: "ID", "JP", "US").
     * @return Objek [Country] jika ditemukan, atau `null` jika kode tidak valid/gagal.
     */
    suspend fun fetchByAlpha2(code: String): Country? {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.length != 2) return null

        return fetchFromUrl("$BASE_URL/codes.alpha_2/$cleanCode")
    }

    /**
     * Fungsi internal untuk mengeksekusi panggilan HTTP GET ke REST Countries v5 dengan otentikasi Bearer token.
     *
     * @param url Alamat URL endpoint lengkap yang akan dipanggil.
     * @return Objek [Country] hasil deserialisasi, atau `null` jika HTTP response non-200 atau terjadi kesalahan.
     */
    private suspend fun fetchFromUrl(url: String): Country? {
        return try {
            val response: HttpResponse = client.get(url) {
                header("Authorization", "Bearer $apiKey")
                header(HttpHeaders.Accept, "application/json")
            }

            if (response.status.value != 200) {
                println("[CountryApiClient] REST Countries v5 status ${response.status.value} for $url")
                return null
            }

            val bodyText = response.bodyAsText()
            if (bodyText.isBlank()) return null

            val parsedResponse: RestCountriesResponse = jsonParser.decodeFromString(bodyText)
            val dto = parsedResponse.data?.objects?.firstOrNull() ?: return null

            val name = dto.names?.common?.ifBlank { null } ?: dto.names?.official ?: "Unknown"
            val capital = dto.capitals.firstOrNull()?.name ?: "N/A"
            val currency = dto.currencies.firstOrNull()
            val currencyCode = currency?.code?.ifBlank { null } ?: "USD"
            val currencyName = currency?.name?.ifBlank { null } ?: "United States Dollar"

            Country(
                name = name,
                capital = capital,
                population = dto.population ?: 0L,
                currencyCode = currencyCode,
                currencyName = currencyName,
                officialName = dto.names?.official ?: name,
                flagUrl = dto.flag?.urlPng ?: dto.flag?.urlSvg
            )
        } catch (e: Exception) {
            println("[CountryApiClient] REST Countries v5 error: ${e.localizedMessage}")
            null
        }
    }
}