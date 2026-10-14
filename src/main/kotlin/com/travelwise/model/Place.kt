package com.travelwise.model

/**
 * Tempat spesifik hasil Overpass API (OpenStreetMap) yang diperkaya foto Unsplash.
 * category: museum | gallery | cafe | attraction | park | historic
 */
data class Place(
    val name: String,
    val category: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val imageUrl: String? = null
) {
    val mapUrl: String?
        get() = if (lat != null && lon != null)
            "https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=17/$lat/$lon"
        else null
}