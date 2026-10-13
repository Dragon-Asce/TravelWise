package com.travelwise.service

import com.travelwise.model.WeatherData

// Implementasi 1: Aktivitas Luar Ruangan (Polimorfisme)
class OutdoorRecommender : ActivityRecommender {
    override fun recommend(weather: WeatherData): List<String> {
        return listOf(
            "Mengunjungi taman kota atau tempat bersejarah terbuka",
            "Wisata kuliner street food lokal",
            "Fotografi pemandangan di sekitar kota"
        )
    }
}

