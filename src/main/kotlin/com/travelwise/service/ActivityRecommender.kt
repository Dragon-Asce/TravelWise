package com.travelwise.service

import com.travelwise.model.WeatherData

// Interface Utama (Abstraksi OOP)
interface ActivityRecommender {
    fun recommend(weather: WeatherData): List<String>
}