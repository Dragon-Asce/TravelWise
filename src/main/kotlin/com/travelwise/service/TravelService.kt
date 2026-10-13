package com.travelwise.service

import com.travelwise.model.Country
import com.travelwise.model.WeatherData
import com.travelwise.network.CountryApiClient
import com.travelwise.network.WeatherApiClient

class TravelService(
    private val countryApiClient: CountryApiClient,
    private val weatherApiClient: WeatherApiClient
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

        return TravelPlanResult.Success(
            country = country,
            weather = weather,
            recommendations = activities
        )
    }
}

// Result State
sealed class TravelPlanResult {
    data class Success(
        val country: Country,
        val weather: WeatherData,
        val recommendations: List<String>
    ) : TravelPlanResult()

    data class Error(val message: String) : TravelPlanResult()
}