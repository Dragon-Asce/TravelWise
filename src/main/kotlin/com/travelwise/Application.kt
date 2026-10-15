package com.travelwise

import com.travelwise.network.CountryApiClient
import com.travelwise.network.ExchangeRateApiClient
import com.travelwise.network.OverpassApiClient
import com.travelwise.network.UnsplashApiClient
import com.travelwise.network.WeatherApiClient
import com.travelwise.plugins.configureRouting
import com.travelwise.service.TravelService
import io.ktor.server.application.*

/**
 * Titik masuk utama (*Main Entry Point*) dari server aplikasi TravelWise.
 *
 * Menginisialisasi dan mendelegasikan siklus hidup aplikasi ke Netty Engine via [io.ktor.server.netty.EngineMain].
 * Konfigurasi port, host, dan modul dibaca secara otomatis dari berkas `resources/application.conf`.
 *
 * @param args Argumen baris perintah (*command line arguments*) yang dilewatkan saat peluncuran aplikasi.
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Modul utama Ktor yang menginisialisasi arsitektur aplikasi TravelWise.
 *
 * Fungsi ekstensi [Application] ini menjalankan serangkaian konfigurasi:
 * 1. **Resolusi Konfigurasi**: Membaca parameter endpoint API dan API Key dari `application.conf`
 *    atau variabel lingkungan (*environment variables*) sistem dengan nilai fallback bawaan.
 * 2. **Instansiasi API Clients**: Menginisialisasi seluruh klien jaringan eksternal
 *    ([CountryApiClient], [WeatherApiClient], [ExchangeRateApiClient], [OverpassApiClient], [UnsplashApiClient]).
 * 3. **Penerapan Dependency Injection**: Menginjeksi instance API client ke dalam lapisan logika bisnis [TravelService].
 * 4. **Konfigurasi Routing**: Mendaftarkan seluruh rute HTTP web dan API melalui [configureRouting].
 *
 * @receiver Instance [Application] server Ktor yang sedang aktif.
 */
fun Application.module() {
    val cfg = environment.config

    // 1. Konfigurasi (application.conf -> env var -> default)
    val exchangeRateApiUrl = cfg.propertyOrNull("api.exchangerate.baseUrl")?.getString()
        ?.takeIf { it.isNotBlank() } ?: "https://open.er-api.com/v6"
    val weatherApiKey = cfg.propertyOrNull("api.openweather.apiKey")?.getString()
        ?.takeIf { it.isNotBlank() } ?: System.getenv("OPENWEATHER_API_KEY") ?: ""
    val unsplashKey = cfg.propertyOrNull("api.unsplash.key")?.getString()
        ?.takeIf { it.isNotBlank() } ?: System.getenv("UNSPLASH_API_KEY") ?: ""

    // 2. Inisialisasi API Clients
    val countryApiClient = CountryApiClient()
    val weatherApiClient = WeatherApiClient(apiKey = weatherApiKey)
    val exchangeRateApiClient = ExchangeRateApiClient(baseUrl = exchangeRateApiUrl)
    val overpassApiClient = OverpassApiClient()
    val unsplashApiClient = UnsplashApiClient(accessKey = unsplashKey)

    // 3. Inject ke TravelService
    val travelService = TravelService(
        countryApiClient, weatherApiClient, exchangeRateApiClient,
        overpassApiClient, unsplashApiClient
    )

    // 4. Routing
    configureRouting(travelService)
}