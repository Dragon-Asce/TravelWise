package com.travelwise.service

import com.travelwise.model.Country
import com.travelwise.model.WeatherData
import com.travelwise.network.CountryApiClient
import com.travelwise.network.WeatherApiClient
import com.travelwise.network.ExchangeRateApiClient

class TravelService(
    private val countryApiClient: CountryApiClient,
    private val weatherApiClient: WeatherApiClient,
    private val exchangeRateApiClient: ExchangeRateApiClient
) {
    suspend fun getTravelPlan(countryName: String): TravelPlanResult {
        val country = countryApiClient.fetchCountry(countryName)
            ?: return TravelPlanResult.Error("Negara '$countryName' tidak ditemukan.")

        val weather = weatherApiClient.fetchWeather(country.capital)
            ?: return TravelPlanResult.Error("Gagal mengambil data cuaca untuk ibu kota ${country.capital}.")

        // Menentukan kelas rekomendasi secara dinamis (Polimorfisme)
        val recommender: ActivityRecommender = if (weather.isRainyOrStormy()) {
            IndoorRecommender()
        } else {
            OutdoorRecommender()
        }

        val activities = recommender.recommend(weather)

        // Estimasi kurs jika target mata uang tersedia
        val rate = exchangeRateApiClient.getExchangeRate("USD", country.currencyCode)
        val dailyBudget = if (rate != null) {
            val localAmount = String.format(java.util.Locale.US, "%,.2f", 50.0 * rate)
            "$50 USD (~$localAmount ${country.currencyCode})"
        } else {
            "$50 USD"
        }

        return TravelPlanResult.Success(
            country = country,
            weather = weather,
            recommendations = activities,
            dailyBudget = dailyBudget
        )
    }
}

// Result State
sealed class TravelPlanResult {
    data class Success(
        val country: Country,
        val weather: WeatherData,
        val recommendations: List<String>,
        val dailyBudget: String = "$50 USD"
    ) : TravelPlanResult()

    data class Error(val message: String) : TravelPlanResult()
}