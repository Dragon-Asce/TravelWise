package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

/**
 * Implementasi strategi rekomendasi untuk kondisi cuaca luar ruangan (*Outdoor Activities*).
 *
 * Menerapkan prinsip **Polimorfisme (Polymorphism)** dari antarmuka [ActivityRecommender].
 * Strategi ini diaktifkan ketika cuaca di lokasi destinasi cerah, sejuk, atau kondusif
 * untuk menjelajahi area terbuka seperti taman kota, situs bersejarah terbuka,
 * atraksi alam, dan wisata kuliner jalanan.
 *
 */
class OutdoorRecommender : ActivityRecommender {

    /** Label identitas visual strategi untuk ditampilkan pada UI antarmuka web. */
    override val label: String = "Aktivitas Outdoor"

    /**
     * Memfilter tempat-tempat wisata yang beroperasi di alam terbuka dan mendiversifikasikannya.
     *
     * Kategori yang dipilih mencakup: `attraction`, `park`, `historic`, dan `nature`.
     *
     * @param places Koleksi tempat wisata mentah hasil penelusuran OpenStreetMap Overpass.
     * @param weather Data telemetri cuaca terkini di lokasi tujuan.
     * @return Maksimal 6 tempat luar ruangan yang terdiversifikasi kategorinya.
     */
    override fun recommend(places: List<Place>, weather: WeatherData): List<Place> =
        places.filter { it.category in listOf("attraction", "park", "historic", "nature") }.diversified(6)

    /**
     * Menyediakan panduan praktis dan tips kenyamanan perjalanan di luar ruangan.
     *
     * Memberikan peringatan khusus apabila suhu lingkungan terlalu panas (>= 32°C)
     * atau terlalu dingin (<= 10°C).
     *
     * @param weather Data telemetri cuaca terkini di lokasi tujuan.
     * @return Daftar rekomendasi aktivitas luar ruangan dan saran adaptasi suhu lingkungan.
     */
    override fun activityTips(weather: WeatherData): List<String> = buildList {
        add("Jelajahi taman kota atau tempat bersejarah terbuka")
        add("Coba wisata kuliner street food lokal")
        add("Ambil foto pemandangan di sekitar kota")
        if (weather.temperature >= 32) add("Cuaca panas: bawa air minum, tabir surya, dan hindari tengah hari")
        if (weather.temperature <= 10) add("Cuaca dingin: kenakan jaket hangat berlapis")
    }
}