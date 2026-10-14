package com.travelwise.plugins

import com.travelwise.model.BudgetTier
import com.travelwise.service.TravelPlanResult
import com.travelwise.service.TravelService
import com.travelwise.view.homeView
import com.travelwise.view.weatherWidget
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import java.util.Locale
import kotlinx.html.*

suspend fun ApplicationCall.handleSearch(travelService: TravelService) {
    val parameters = if (request.httpMethod.value == "POST") {
        receiveParameters()
    } else {
        request.queryParameters
    }

    val countryQuery = (parameters["country"] ?: parameters["destination"])?.trim() ?: ""
    val tierParam = parameters["tier"]?.trim() ?: "MODERATE"
    val budgetTier = try {
        BudgetTier.valueOf(tierParam.uppercase())
    } catch (e: Exception) {
        BudgetTier.MODERATE
    }

    if (countryQuery.isEmpty()) {
        respondHtml {
            body {
                div("border-2 border-red-500 bg-red-950/80 p-4 font-mono text-xs text-red-400 uppercase tracking-widest") {
                    +">> ERROR: PARAMETER_QUERY_EMPTY. PLEASE_ENTER_COUNTRY_NAME."
                }
            }
        }
        return
    }

    when (val result = travelService.getTravelPlan(countryQuery, budgetTier)) {
        is TravelPlanResult.Success -> {
            respondHtml {
                body {
                    // Formatter Anggaran Harian
                    val formattedDailyLocal = result.budget.dailyLocal?.let {
                        String.format(Locale.US, "%,.2f", it)
                    } ?: "N/A"

                    val formattedDailyBudget = if (result.budget.localCurrency.equals("USD", ignoreCase = true)) {
                        "$${result.budget.dailyUsd} USD / hari"
                    } else {
                        "$${result.budget.dailyUsd} USD (~$formattedDailyLocal ${result.budget.localCurrency}) / hari"
                    }

                    // Formatter Rekomendasi Aktivitas
                    val formattedActivities = if (result.tips.isNotEmpty()) {
                        result.tips.joinToString(". ")
                    } else if (result.places.isNotEmpty()) {
                        "Kunjungi: " + result.places.take(3).joinToString(", ") { it.name }
                    } else {
                        "Eksplorasi tempat wisata lokal dan nikmati kuliner khas setempat."
                    }

                    // Panggil Widget UI
                    weatherWidget(
                        countryName = result.country.name,
                        capital = result.country.capital,
                        population = String.format(Locale.US, "%,d", result.country.population),
                        currencyCode = result.country.currencyCode,
                        temperature = result.weather.temperature,
                        humidity = result.weather.humidity,
                        weatherDesc = result.weather.description,
                        recommendedActivity = formattedActivities,
                        dailyBudget = formattedDailyBudget,
                        seasonName = if (result.isIndoor) "Rekomendasi Indoor" else "Rekomendasi Outdoor",
                        seasonDesc = "Engine: ${result.recommenderLabel}",
                        destinationDescription = "Analisis destinasi otomatis berbasis telemetri cuaca dan lokasi.",
                        imageUrl = result.bannerUrl.ifBlank {
                            "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80"
                        },
                        targetCity = result.targetCity
                    )
                }
            }
        }
        is TravelPlanResult.Error -> {
            respondHtml {
                body {
                    div("border-2 border-amber-500 bg-amber-950/80 p-4 font-mono text-xs text-amber-400 uppercase tracking-widest") {
                        +">> TELEMETRY_FETCH_FAILED: ${result.message}"
                    }
                }
            }
        }
    }
}

fun Application.configureRouting(travelService: TravelService) {
    routing {
        get("/") {
            call.respondHtml {
                homeView()
            }
        }

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