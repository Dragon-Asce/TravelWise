package com.travelwise

import com.travelwise.network.CountryApiClient
import com.travelwise.network.ExchangeRateApiClient
import com.travelwise.network.OverpassApiClient
import com.travelwise.network.UnsplashApiClient
import com.travelwise.network.WeatherApiClient
import com.travelwise.plugins.configureRouting
import com.travelwise.service.TravelService
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

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