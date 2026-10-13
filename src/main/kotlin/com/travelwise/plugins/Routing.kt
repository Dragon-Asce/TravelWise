package com.travelwise.plugins

import com.travelwise.service.TravelPlanResult
import com.travelwise.service.TravelService
import com.travelwise.view.HomeView
import com.travelwise.view.WeatherWidget
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

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
            val formParameters = call.receiveParameters()
            val countryQuery = formParameters["country"]?.trim() ?: ""

            if (countryQuery.isEmpty()) {
                call.respondHtml {
                    WeatherWidget.renderError(this, "Silakan masukkan nama negara.")
                }
                return@post
            }

            // Memanggil Service Layer
            when (val result = travelService.getTravelPlan(countryQuery)) {
                is TravelPlanResult.Success -> {
                    call.respondHtml {
                        WeatherWidget.renderSuccess(this, result.country, result.weather, result.recommendations)
                    }
                }
                is TravelPlanResult.Error -> {
                    call.respondHtml {
                        WeatherWidget.renderError(this, result.message)
                    }
                }
            }
        }
    }
}