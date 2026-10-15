package com.travelwise.service

import com.travelwise.model.BudgetLine
import com.travelwise.model.BudgetPlan
import com.travelwise.model.BudgetTier
import com.travelwise.model.Country
import com.travelwise.model.Place
import com.travelwise.model.WeatherData
import com.travelwise.network.CountryApiClient
import com.travelwise.network.ExchangeRateApiClient
import com.travelwise.network.OverpassApiClient
import com.travelwise.network.UnsplashApiClient
import com.travelwise.network.WeatherApiClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

/**
 * Layanan inti (*core business service*) yang mengorkestrasikan seluruh logika perencana perjalanan cerdas.
 *
 * Menerapkan pola **Dependency Injection** berbasis konstruktor dan orkestrasi asinkron tingkat tinggi
 * menggunakan Kotlin Coroutines (`async`/`awaitAll`), dengan alur kerja:
 * 1. Menemukan profil negara dan koordinat/telemetri cuaca kota tujuan.
 * 2. Menerapkan pemilihan strategi rekomendasi polimorfik ([ActivityRecommender]) berdasarkan kondisi cuaca dan suhu ekstrem.
 * 3. Menghitung musim aktual (*astronomical season*) berdasarkan posisi garis lintang (*latitude*) dan kalender bulan.
 * 4. Menjalankan kueri paralel untuk: kurs valuta asing ([ExchangeRateApiClient]), foto banner ([UnsplashApiClient]),
 *    dan Point of Interest OpenStreetMap ([OverpassApiClient]).
 * 5. Memperkaya setiap POI dengan foto resolusi tinggi secara paralel.
 * 6. Mengalkulasi estimasi anggaran harian dalam USD, valuta lokal negara tujuan, dan mata uang Rupiah (IDR).
 *
 * @property countryApiClient Klien untuk mengakses data geografis dan kedaulatan negara.
 * @property weatherApiClient Klien untuk mengakses data telemetri cuaca real-time.
 * @property exchangeRateApiClient Klien untuk mengambil kurs nilai tukar valas global.
 * @property overpassApiClient Klien untuk mencari tempat wisata di sekitar koordinat kota via OpenStreetMap.
 * @property unsplashApiClient Klien untuk mencari fotografi resolusi tinggi destinasi perjalanan.
 */
class TravelService(
    private val countryApiClient: CountryApiClient,
    private val weatherApiClient: WeatherApiClient,
    private val exchangeRateApiClient: ExchangeRateApiClient,
    private val overpassApiClient: OverpassApiClient,
    private val unsplashApiClient: UnsplashApiClient
) {
    /**
     * Memproses permintaan perencanaan perjalanan secara menyeluruh berdasarkan kata kunci destinasi dan tingkatan anggaran.
     *
     * @param query Kata kunci pencarian, dapat berupa nama negara (misal: "Japan") atau nama kota (misal: "Tokyo", "Paris", "Bali").
     * @param tier Tingkatan anggaran yang dipilih pengguna ([BudgetTier.BACKPACKER], [BudgetTier.MODERATE], atau [BudgetTier.LUXURY]).
     * @return [TravelPlanResult.Success] jika seluruh data berhasil dihimpun, atau [TravelPlanResult.Error] jika terjadi kegagalan.
     */
    suspend fun getTravelPlan(
        query: String,
        tier: BudgetTier = BudgetTier.MODERATE
    ): TravelPlanResult = coroutineScope {
        var country = countryApiClient.fetchCountry(query)
        val weather: WeatherData
        val targetCity: String

        if (country != null) {
            val capitalCity = if (country.capital != "N/A" && country.capital.isNotBlank()) country.capital else query
            val fetchedWeather = weatherApiClient.fetchWeather(capitalCity)
                ?: weatherApiClient.fetchWeather(query)
                ?: return@coroutineScope TravelPlanResult.Error(
                    "Gagal mengambil data cuaca untuk ${capitalCity}. Periksa nama kota dan OPENWEATHER_API_KEY."
                )
            weather = fetchedWeather
            targetCity = if (country.capital != "N/A" && country.capital.isNotBlank()) country.capital else weather.cityName
        } else {
            val cityWeather = weatherApiClient.fetchWeather(query)
                ?: return@coroutineScope TravelPlanResult.Error("Destinasi '$query' tidak ditemukan.")
            weather = cityWeather
            targetCity = cityWeather.cityName

            val alpha2 = cityWeather.countryCode
            val countryByCode = if (!alpha2.isNullOrBlank()) {
                countryApiClient.fetchByAlpha2(alpha2)
            } else null

            country = countryByCode ?: Country(
                name = cityWeather.cityName,
                capital = cityWeather.cityName,
                population = 0L,
                currencyCode = "USD"
            )
        }

        // LOGIKA INDOOR/OUTDOOR
        val desc = weather.description.lowercase()
        val isBadWeather = desc.contains("rain") || desc.contains("storm") ||
                desc.contains("drizzle") || desc.contains("snow") ||
                desc.contains("shower") || desc.contains("thunder")
        val isExtremeTemp = weather.temperature < 12.0 || weather.temperature > 34.0
        val isIndoor = weather.isRainyOrStormy() || isBadWeather || isExtremeTemp

        val recommender: ActivityRecommender = if (isIndoor) IndoorRecommender() else OutdoorRecommender()

        // DETEKSI MUSIM AKTUAL BERSAARKAN KOORDINAT & BULAN
        val seasonName = calculateCurrentSeason(weather.lat)

        // Jalankan paralel: kurs Exchange API, banner Unsplash, dan POI Overpass
        val ratesDeferred = async { exchangeRateApiClient.fetchData("USD") }
        val bannerDeferred = async {
            unsplashApiClient.fetchImage("${weather.cityName} travel", country.name)
        }
        val placesDeferred = async {
            val lat = weather.lat
            val lon = weather.lon
            if (lat != null && lon != null) overpassApiClient.fetchPlaces(lat, lon) else emptyList()
        }

        val picked = recommender.recommend(placesDeferred.await(), weather)

        val places: List<Place> = picked.map { place ->
            async {
                place.copy(
                    imageUrl = unsplashApiClient.fetchImage(
                        "${place.name} ${weather.cityName}",
                        "${place.category} ${weather.cityName}"
                    )
                )
            }
        }.awaitAll()

        val budget = buildBudget(tier, country, ratesDeferred.await())

        TravelPlanResult.Success(
            country = country,
            weather = weather,
            isIndoor = isIndoor,
            seasonName = seasonName,
            recommenderLabel = recommender.label,
            tips = recommender.activityTips(weather),
            places = places,
            bannerUrl = bannerDeferred.await(),
            budget = budget,
            targetCity = targetCity
        )
    }

    /**
     * Menghitung musim saat ini berdasarkan koordinat lintang bumi (*latitude*) dan kalender bulan berjalan.
     *
     * Logika pembagian:
     * - Daerah Khatulistiwa / Tropis (-10° s/d 10° lintang): Musim Kemarau (April - Oktober) vs Musim Hujan.
     * - Belahan Bumi Utara (> 10° lintang): Winter (Des-Feb), Spring (Mar-Mei), Summer (Jun-Agu), Autumn (Sep-Nov).
     * - Belahan Bumi Selatan (<-10° lintang): Kebalikan dari musim di belahan bumi utara.
     *
     * @param lat Koordinat garis lintang (*latitude*) kota destinasi.
     * @return Label string nama musim dalam huruf kapital (contoh: "MUSIM DINGIN (WINTER)", "MUSIM KEMARAU").
     */
    private fun calculateCurrentSeason(lat: Double?): String {
        val month = LocalDate.now().monthValue
        val latitude = lat ?: 0.0

        // Daerah Tropis (misal Indonesia: -10° s/d 10°)
        if (latitude in -10.0..10.0) {
            return if (month in 4..10) "MUSIM KEMARAU" else "MUSIM HUJAN"
        }

        // Belahan Bumi Utara (Northern Hemisphere)
        if (latitude > 10.0) {
            return when (month) {
                12, 1, 2 -> "MUSIM DINGIN (WINTER)"
                in 3..5 -> "MUSIM SEMI (SPRING)"
                in 6..8 -> "MUSIM PANAS (SUMMER)"
                else -> "MUSIM GUGUR (AUTUMN)"
            }
        }

        // Belahan Bumi Selatan (Southern Hemisphere)
        return when (month) {
            12, 1, 2 -> "MUSIM PANAS (SUMMER)"
            in 3..5 -> "MUSIM GUGUR (AUTUMN)"
            in 6..8 -> "MUSIM DINGIN (WINTER)"
            else -> "MUSIM SEMI (SPRING)"
        }
    }

    /**
     * Membangun model kalkulasi anggaran harian lengkap dengan pembagian persentase pos biaya.
     *
     * Alokasi pos pengeluaran dihitung dari total biaya harian USD:
     * - Akomodasi: 40%
     * - Makan & Minum: 30%
     * - Transportasi: 15%
     * - Aktivitas & Tiket: 15%
     *
     * @param tier Tingkatan anggaran yang dipilih pengguna ([BudgetTier]).
     * @param country Data negara destinasi untuk mendapatkan kode mata uang resmi.
     * @param rates Peta nilai tukar valas berbasis USD dari ExchangeRate API.
     * @return Objek model [BudgetPlan] yang telah dikonversi ke valuta lokal dan Rupiah (IDR).
     */
    private fun buildBudget(tier: BudgetTier, country: Country, rates: Map<String, Double>?): BudgetPlan {
        val idrRate = rates?.get("IDR") ?: 15500.0
        val isIdrCountry = country.currencyCode.equals("IDR", ignoreCase = true)

        val localRate = if (isIdrCountry) idrRate else rates?.get(country.currencyCode)
        val usd = tier.dailyUsd

        fun line(label: String, share: Double) = BudgetLine(
            label = label,
            usd = usd * share,
            local = localRate?.let { usd * share * it }
        )

        val dailyLocalVal = localRate?.let { usd * it }
        val dailyIdrVal = if (isIdrCountry) dailyLocalVal else usd * idrRate

        return BudgetPlan(
            tier = tier,
            dailyUsd = usd,
            localCurrency = country.currencyCode,
            dailyLocal = dailyLocalVal,
            dailyIdr = dailyIdrVal,
            localPerUsd = localRate,
            breakdown = listOf(
                line("Akomodasi", 0.40),
                line("Makan & minum", 0.30),
                line("Transportasi", 0.15),
                line("Aktivitas & tiket", 0.15)
            )
        )
    }
}

/**
 * Status hasil pemrosesan rencana perjalanan wisata (*Result State Pattern*).
 *
 * Menerapkan pola *Sealed Class* yang aman terhadap tipe (*type-safe*) untuk membedakan
 * antara eksekusi yang sukses dan eksekusi yang menghasilkan pesan galat (*error*).
 *
 */
sealed class TravelPlanResult {

    /**
     * Status sukses yang memuat seluruh payload informasi perjalanan yang berhasil diagregasi.
     *
     * @property country Data profil kedaulatan negara destinasi.
     * @property weather Data telemetri pengamatan cuaca *real-time*.
     * @property isIndoor Menandakan apakah kondisi cuaca mengharuskan rekomendasi aktivitas *indoor*.
     * @property seasonName Nama musim aktual hasil estimasi koordinat dan kalender (contoh: "MUSIM HUJAN").
     * @property recommenderLabel Label deskriptif strategi rekomendasi ("Aktivitas Indoor" / "Aktivitas Outdoor").
     * @property tips Kumpulan kiat dan saran aktivitas perjalanan yang adaptif terhadap cuaca.
     * @property places Daftar tempat wisata nyata (POI) hasil kurasi OpenStreetMap & Unsplash.
     * @property bannerUrl Tautan gambar fotografi lanskap kota destinasi.
     * @property budget Rencana estimasi anggaran harian dalam berbagai mata uang.
     * @property targetCity Nama kota spesifik yang menjadi fokus utama pengamatan.
     */
    data class Success(
        val country: Country,
        val weather: WeatherData,
        val isIndoor: Boolean,
        val seasonName: String,
        val recommenderLabel: String,
        val tips: List<String>,
        val places: List<Place>,
        val bannerUrl: String,
        val budget: BudgetPlan,
        val targetCity: String = country.capital
    ) : TravelPlanResult()

    /**
     * Status gagal yang mengembalikan pesan kesalahan untuk ditampilkan kepada pengguna.
     *
     * @property message Pesan galat deskriptif mengenai kendala yang terjadi (misal: "Destinasi tidak ditemukan").
     */
    data class Error(val message: String) : TravelPlanResult()
}