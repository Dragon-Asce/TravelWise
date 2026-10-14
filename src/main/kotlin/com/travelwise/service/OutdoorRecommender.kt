package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

// Implementasi 2: Aktivitas Luar Ruangan (Polimorfisme)
class OutdoorRecommender : ActivityRecommender {
    override val label = "Aktivitas Outdoor"

    override fun recommend(places: List<Place>, weather: WeatherData): List<Place> =
        places.filter { it.category in listOf("attraction", "park", "historic", "nature") }.diversified(6)

    override fun activityTips(weather: WeatherData): List<String> = buildList {
        add("Jelajahi taman kota atau tempat bersejarah terbuka")
        add("Coba wisata kuliner street food lokal")
        add("Ambil foto pemandangan di sekitar kota")
        if (weather.temperature >= 32) add("Cuaca panas: bawa air minum, tabir surya, dan hindari tengah hari")
        if (weather.temperature <= 10) add("Cuaca dingin: kenakan jaket hangat berlapis")
    }
}