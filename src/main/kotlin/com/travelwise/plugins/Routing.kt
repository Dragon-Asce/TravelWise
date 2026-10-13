package com.travelwise.plugins

import com.travelwise.service.TravelPlanResult
import com.travelwise.service.TravelService
import com.travelwise.view.HomeView
import com.travelwise.view.WeatherWidget
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

suspend fun ApplicationCall.handleSearch(travelService: TravelService) {
    val parameters = if (request.httpMethod.value == "POST") {
        receiveParameters()
    } else {
        request.queryParameters
    }
    val countryQuery = (parameters["country"] ?: parameters["destination"])?.trim() ?: ""

    if (countryQuery.isEmpty()) {
        respondHtml {
            WeatherWidget.renderError(this, "Silakan masukkan nama negara.")
        }
        return
    }

    when (val result = travelService.getTravelPlan(countryQuery)) {
        is TravelPlanResult.Success -> {
            respondHtml {
                WeatherWidget.renderSuccess(
                    this,
                    result.country,
                    result.weather,
                    result.recommendations,
                    result.dailyBudget
                )
            }
        }
        is TravelPlanResult.Error -> {
            respondHtml {
                WeatherWidget.renderError(this, result.message)
            }
        }
    }
}

fun Application.configureRouting(travelService: TravelService) {
    routing {
        // Endpoint 1: Menampilkan Halaman Utama Web
        get("/") {
            call.respondHtml {
                HomeView.render(this)
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