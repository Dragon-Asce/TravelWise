package com.travelwise.service

import com.travelwise.model.Place
import com.travelwise.model.WeatherData

/**
 * Kontrak antarmuka (*interface*) utama yang merepresentasikan strategi rekomendasi aktivitas perjalanan.
 *
 * Menerapkan prinsip **Abstraksi (Abstraction)** dan **Strategy Pattern** dalam OOP,
 * di mana penentuan jenis tempat wisata dan saran aktivitas dipisahkan dari implementasi konkretnya.
 * Implementasi konkret (seperti [IndoorRecommender] dan [OutdoorRecommender]) akan dipilih secara dinamis
 * (*runtime polymorphism*) berdasarkan kondisi telemetri cuaca aktual.
 */
interface ActivityRecommender {
    /**
     * Label tema rekomendasi yang ditampilkan pada antarmuka web (contoh: "Aktivitas Indoor", "Aktivitas Outdoor").
     */
    val label: String

    /**
     * Menyaring dan memilih tempat nyata (hasil kueri Overpass API) yang paling relevan dengan kondisi cuaca.
     *
     * @param places Daftar seluruh tempat wisata yang ditemukan di sekitar koordinat destinasi.
     * @param weather Data telemetri cuaca aktual di destinasi perjalanan.
     * @return Daftar tempat terpilih yang sudah didiversifikasi jenis kategorinya.
     */
    fun recommend(places: List<Place>, weather: WeatherData): List<Place>

    /**
     * Menghasilkan daftar kiat atau saran aktivitas praktis yang disesuaikan dengan kondisi cuaca dan suhu.
     *
     * @param weather Data telemetri cuaca terkini untuk destinasi yang diperiksa.
     * @return Daftar teks saran aktivitas dalam bahasa Indonesia.
     */
    fun activityTips(weather: WeatherData): List<String>
}

/**
 * Fungsi ekstensi koleksi untuk mengambil tempat secara bergantian per kategori agar hasil kurasi bervariasi.
 *
 * Mencegah hasil rekomendasi didominasi oleh satu kategori saja (misalnya 6 kafe berturut-turut).
 * Algoritma melakukan *round-robin* melintasi setiap grup kategori hingga jumlah tempat mencapai [limit].
 *
 * @receiver Daftar tempat wisata asal ([List] of [Place]).
 * @param limit Batas maksimal jumlah tempat yang ingin dikembalikan.
 * @return Daftar tempat wisata yang telah didiversifikasi secara proporsional.
 */
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