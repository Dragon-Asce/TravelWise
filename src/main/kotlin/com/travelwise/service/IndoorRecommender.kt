package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

/**
 * Implementasi strategi rekomendasi untuk kondisi cuaca dalam ruangan (*Indoor Activities*).
 *
 * Menerapkan prinsip **Polimorfisme (Polymorphism)** dari antarmuka [ActivityRecommender].
 * Strategi ini diaktifkan ketika cuaca di lokasi destinasi buruk, hujan, badai, bersalju,
 * atau bersuhu ekstrem, sehingga menyarankan tempat-tempat terlindung seperti museum,
 * galeri seni, kafe hangat, dan pusat perbelanjaan.
 *
 */
class IndoorRecommender : ActivityRecommender {

    /** Label identitas visual strategi untuk ditampilkan pada UI antarmuka web. */
    override val label: String = "Aktivitas Indoor"

    /**
     * Memfilter tempat-tempat wisata yang beroperasi di dalam ruangan dan mendiversifikasikannya.
     *
     * Kategori yang dipilih mencakup: `museum`, `cafe`, `gallery`, dan `shopping`.
     *
     * @param places Koleksi tempat wisata mentah hasil penelusuran OpenStreetMap Overpass.
     * @param weather Data telemetri cuaca terkini di lokasi tujuan.
     * @return Maksimal 6 tempat dalam ruangan yang terdiversifikasi kategorinya.
     */
    override fun recommend(places: List<Place>, weather: WeatherData): List<Place> =
        places.filter { it.category in listOf("museum", "cafe", "gallery", "shopping") }.diversified(6)

    /**
     * Menyediakan panduan praktis dan tips keselamatan perjalanan saat cuaca kurang mendukung.
     *
     * @param weather Data telemetri cuaca terkini di lokasi tujuan.
     * @return Daftar rekomendasi aktivitas terlindung dan tips perlengkapan seperti payung/jas hujan.
     */
    override fun activityTips(weather: WeatherData): List<String> = buildList {
        add("Kunjungi museum atau galeri seni indoor")
        add("Nongkrong di kafe lokal sambil menunggu cuaca reda")
        add("Berbelanja di pusat perbelanjaan utama")
        if (weather.isRainyOrStormy()) add("Bawa payung atau jas hujan untuk perjalanan antar lokasi")
    }
}