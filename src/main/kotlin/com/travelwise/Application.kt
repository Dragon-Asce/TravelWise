package com.travelwise

import com.travelwise.network.CountryApiClient
import com.travelwise.network.ExchangeRateApiClient
import com.travelwise.network.WeatherApiClient
import com.travelwise.plugins.configureRouting
import com.travelwise.service.TravelService
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // 1. Baca konfigurasi dari application.conf (opsional, jika tidak memakai default value)
    val countryApiUrl = environment.config.propertyOrNull("api.restcountries.baseUrl")?.getString() 
        ?: "https://restcountries.com/v3.1"
    val exchangeRateApiUrl = environment.config.propertyOrNull("api.exchangerate.baseUrl")?.getString() 
        ?: "https://open.er-api.com/v6"
    val weatherApiKey = environment.config.propertyOrNull("api.openweather.apiKey")?.getString()
        ?: System.getenv("OPENWEATHER_API_KEY")
        ?: ""

    // 2. Inisialisasi API Clients
    val countryApiClient = CountryApiClient(baseUrl = countryApiUrl)
    val exchangeRateApiClient = ExchangeRateApiClient(baseUrl = exchangeRateApiUrl)
    val weatherApiClient = WeatherApiClient(apiKey = weatherApiKey)

    // 3. Inject ke TravelService
    val travelService = TravelService(countryApiClient, weatherApiClient, exchangeRateApiClient)

    // 4. Set Routing
    configureRouting(travelService)
}