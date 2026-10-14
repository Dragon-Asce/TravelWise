package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

// Implementasi 1: Aktivitas Dalam Ruangan (Polimorfisme)
class IndoorRecommender : ActivityRecommender {
    override val label = "Aktivitas Indoor"

    override fun recommend(places: List<Place>, weather: WeatherData): List<Place> =
        places.filter { it.category in listOf("museum", "cafe", "gallery", "shopping") }.diversified(6)

    override fun activityTips(weather: WeatherData): List<String> = buildList {
        add("Kunjungi museum atau galeri seni indoor")
        add("Nongkrong di kafe lokal sambil menunggu cuaca reda")
        add("Berbelanja di pusat perbelanjaan utama")
        if (weather.isRainyOrStormy()) add("Bawa payung atau jas hujan untuk perjalanan antar lokasi")
    }
}