package com.travelwise.model

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val condition: String,
    val description: String,
    val windSpeed: Double = 0.0,
    val lat: Double? = null,
    val lon: Double? = null
) {
    fun isRainyOrStormy(): Boolean {
        val cond = condition.lowercase()
        return cond.contains("rain") ||
               cond.contains("storm") ||
               cond.contains("drizzle") ||
               cond.contains("thunderstorm")
    }
}