package com.travelwise.service

import com.travelwise.model.WeatherData

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

