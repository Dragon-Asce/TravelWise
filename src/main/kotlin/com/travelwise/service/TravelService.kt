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

class TravelService(
    private val countryApiClient: CountryApiClient,
    private val weatherApiClient: WeatherApiClient,
    private val exchangeRateApiClient: ExchangeRateApiClient,
    private val overpassApiClient: OverpassApiClient,
    private val unsplashApiClient: UnsplashApiClient
) {
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

// Result State
sealed class TravelPlanResult {
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

    data class Error(val message: String) : TravelPlanResult()
}