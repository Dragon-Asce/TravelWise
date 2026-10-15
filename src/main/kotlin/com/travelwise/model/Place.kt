package com.travelwise.model

/**
 * Entitas domain yang merepresentasikan Point of Interest (POI) atau tempat wisata nyata.
 *
 * Diperoleh dari Overpass API (OpenStreetMap) di sekitar koordinat kota tujuan dan
 * diperkaya secara asinkron dengan tautan gambar fotografi dari Unsplash API.
 *
 * @property name Nama resmi tempat atau atraksi (contoh: "Tokyo National Museum", "Senso-ji").
 * @property category Kategori jenis tempat (misal: "museum", "gallery", "cafe", "attraction", "park", "historic").
 * @property lat Koordinat garis lintang (*latitude*) posisi tempat pada peta bumi.
 * @property lon Koordinat garis bujur (*longitude*) posisi tempat pada peta bumi.
 * @property imageUrl Tautan URL gambar ilustrasi tempat wisata (dari Unsplash atau data SVG dinamis).
 */
data class Place(
    val name: String,
    val category: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val imageUrl: String? = null
) {
    /**
     * Tautan URL menuju OpenStreetMap interaktif dengan pin penanda tepat pada titik koordinat lokasi.
     *
     * Menghasilkan tautan web jika [lat] dan [lon] tersedia, atau bernilai `null` jika koordinat tidak valid.
     */
    val mapUrl: String?
        get() = if (lat != null && lon != null)
            "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=17/$lat/$lon"
        else null
}