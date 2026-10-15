package com.travelwise.model

/**
 * Entitas domain yang merepresentasikan kondisi telemetri cuaca *real-time* suatu kota destinasi.
 *
 * Data ini diekstrak dari OpenWeatherMap API dan menjadi basis utama bagi mesin rekomendasi
 * aktivitas ([com.travelwise.service.ActivityRecommender]) dalam menentukan apakah wisatawan
 * sebaiknya melakukan aktivitas *indoor* atau *outdoor*.
 *
 * @property cityName Nama kota lokasi pengamatan cuaca (contoh: "Tokyo", "Paris", "Bandung").
 * @property temperature Nilai suhu udara saat ini dalam satuan derajat Celsius (°C).
 * @property humidity Persentase kelembapan udara relatif (0 - 100%).
 * @property condition Kategori kondisi cuaca utama (contoh: "Clear", "Rain", "Clouds", "Snow", "Thunderstorm").
 * @property description Keterangan deskripsi kondisi langit/cuaca secara detail (contoh: "light rain", "overcast clouds").
 * @property windSpeed Kecepatan hembusan angin saat ini dalam satuan meter per detik (m/s).
 * @property lat Koordinat garis lintang (*latitude*) lokasi pengamatan cuaca.
 * @property lon Koordinat garis bujur (*longitude*) lokasi pengamatan cuaca.
 * @property countryCode Kode negara dua huruf standar ISO 3166-1 alpha-2 tempat kota berada (contoh: "JP", "ID", "FR").
 */
data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val condition: String,
    val description: String,
    val windSpeed: Double = 0.0,
    val lat: Double? = null,
    val lon: Double? = null,
    val countryCode: String? = null
) {
    /**
     * Memeriksa apakah kondisi cuaca saat ini dikategorikan hujan, gerimis, atau badai petir.
     *
     * Metode ini memeriksa kata kunci cuaca penting pada [condition] seperti "rain", "storm",
     * "drizzle", dan "thunderstorm" tanpa terpengaruh huruf besar/kecil (*case-insensitive*).
     *
     * @return `true` jika cuaca mengindikasikan hujan atau badai, `false` jika sebaliknya.
     */
    fun isRainyOrStormy(): Boolean {
        val cond = condition.lowercase()
        return cond.contains("rain") ||
               cond.contains("storm") ||
               cond.contains("drizzle") ||
               cond.contains("thunderstorm")
    }
}