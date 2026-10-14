package com.travelwise.plugins

import com.travelwise.service.TravelPlanResult
import com.travelwise.service.TravelService
import com.travelwise.view.homeView
import com.travelwise.view.weatherWidget
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.html.*

suspend fun ApplicationCall.handleSearch(travelService: TravelService) {
    val parameters = if (request.httpMethod.value == "POST") {
        receiveParameters()
    } else {
        request.queryParameters
    }
    val countryQuery = (parameters["country"] ?: parameters["destination"])?.trim() ?: ""

    if (countryQuery.isEmpty()) {
        respondHtml {
            body {
                div("p-4 mb-4 text-sm text-red-400 rounded-2xl bg-red-950/50 border border-red-500/20 text-center backdrop-blur-md") {
                    +"Silakan masukkan nama negara atau kota."
                }
            }
        }
        return
    }

    when (val result = travelService.getTravelPlan(countryQuery)) {
        is TravelPlanResult.Success -> {
            respondHtml {
                body {
                    val formattedPopulation = String.format(java.util.Locale.US, "%,d", result.country.population)
                    weatherWidget(
                        countryName = result.country.name,
                        capital = result.country.capital,
                        population = formattedPopulation,
                        currencyCode = result.country.currencyCode,
                        temperature = result.weather.temperature,
                        humidity = result.weather.humidity,
                        weatherDesc = result.weather.description,
                        // Menggabungkan list rekomendasi menjadi satu string
                        recommendedActivity = result.recommendations.joinToString(". "),
                        dailyBudget = result.dailyBudget,
                        isOutdoor = !result.weather.isRainyOrStormy(),
                        targetCity = result.country.targetCity
                    )
                }
            }
        }
        is TravelPlanResult.Error -> {
            respondHtml {
                body {
                    div("p-4 mb-4 text-sm text-amber-400 rounded-2xl bg-amber-950/50 border border-amber-500/20 text-center backdrop-blur-md animate-slide-up") {
                        +result.message
                    }
                }
            }
        }
    }
}

fun Application.configureRouting(travelService: TravelService) {
    routing {
        // Endpoint 1: Menampilkan Halaman Utama Web
        get("/") {
            call.respondHtml {
                homeView()
            }
        }

        // Endpoint 2: Menerima Input Pencarian dari Form (Support HTMX / AJAX)
        post("/search") {
            call.handleSearch(travelService)
        }
        get("/search") {
            call.handleSearch(travelService)
        }
        post("/api/search") {
            call.handleSearch(travelService)
        }
        get("/api/search") {
            call.handleSearch(travelService)
        }
    }
}