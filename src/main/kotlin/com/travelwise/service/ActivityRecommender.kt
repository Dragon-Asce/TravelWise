package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

// Interface Utama (Abstraksi OOP)
interface ActivityRecommender {
    /** Label tema rekomendasi untuk ditampilkan di UI. */
    val label: String

    /** Memilih tempat nyata (hasil Overpass) yang cocok dengan kondisi cuaca. */
    fun recommend(places: List<Place>, weather: WeatherData): List<Place>

    /** Saran aktivitas tekstual sesuai cuaca. */
    fun activityTips(weather: WeatherData): List<String>
}

/** Ambil bergantian per kategori agar hasil bervariasi (bukan 6 kafe semua). */
internal fun List<Place>.diversified(limit: Int): List<Place> {
    val iterators = groupBy { it.category }.values.map { it.iterator() }
    val result = mutableListOf<Place>()
    while (result.size < limit && iterators.any { it.hasNext() }) {
        for (it in iterators) {
            if (it.hasNext() && result.size < limit) result.add(it.next())
        }
    }
    return result
}