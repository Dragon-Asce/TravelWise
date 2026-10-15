package com.travelwise.network

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Penyedia instance [HttpClient] terpusat berbasis pola desain Singleton.
 *
 * Menggunakan mesin asynchronous Ktor CIO (*Coroutine-based I/O*) yang efisien
 * untuk menangani seluruh panggilan HTTP outbound ke berbagai Public API eksternal
 * (OpenWeather, REST Countries, ExchangeRate, Overpass, dan Unsplash).
 *
 * Dilengkapi dengan:
 * - **ContentNegotiation**: Deserialisasi JSON otomatis menggunakan Kotlinx Serialization.
 * - **HttpTimeout**: Pembatasan durasi request (10 detik) dan koneksi (5 detik) untuk mencegah *thread hanging*.
 * - **UserAgent**: Header identitas client `TravelWise/2.0` untuk kepatuhan terhadap kebijakan API pihak ketiga.
 *
 * @author Farhan
 */
object HttpClients {

    /**
     * Instance [HttpClient] global yang diinisialisasi secara malas (*thread-safe lazy initialization*).
     *
     * Berbagi koneksi pool dan konfigurasi yang sama di seluruh instance [ApiClient]
     * guna menghemat penggunaan soket dan memori sistem.
     */
    val default: HttpClient by lazy {
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; isLenient = true })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10_000
                connectTimeoutMillis = 5_000
            }
            install(UserAgent) { agent = "TravelWise/2.0" }
        }
    }
}