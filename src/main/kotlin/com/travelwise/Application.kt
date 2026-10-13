package com.travelwise

import com.travelwise.network.CountryApiClient
import com.travelwise.network.WeatherApiClient
import com.travelwise.plugins.configureRouting
import com.travelwise.service.TravelService
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    // Inisialisasi API Client & Service
    val countryApiClient = CountryApiClient()
    val weatherApiClient = WeatherApiClient()
    val travelService = TravelService(countryApiClient, weatherApiClient)

    // Run Ktor Web Server di port 8080
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting(travelService)
    }.start(wait = true)
}