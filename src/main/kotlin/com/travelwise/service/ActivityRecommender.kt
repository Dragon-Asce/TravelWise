package com.travelwise.service

import com.travelwise.model.WeatherData

// Interface Utama (Abstraksi OOP)
interface ActivityRecommender {
    fun recommend(weather: WeatherData): List<String>
}

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

// Implementasi 2: Aktivitas Dalam Ruangan (Polimorfisme)
class IndoorRecommender : ActivityRecommender {
    override fun recommend(weather: WeatherData): List<String> {
        return listOf(
            "Mengunjungi museum atau galeri seni indoor",
            "Nongkrong di kafe lokal atau perpustakaan kota",
            "Berbelanja di pusat perbelanjaan utama"
        )
    }
}