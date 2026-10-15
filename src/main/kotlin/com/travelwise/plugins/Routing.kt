package com.travelwise.plugins

import com.travelwise.model.BudgetTier
import com.travelwise.service.TravelPlanResult
import com.travelwise.service.TravelService
import com.travelwise.view.PointOfInterest
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
    val budgetTier = BudgetTier.fromParam(tierParam)

    if (countryQuery.isEmpty()) {
        respondHtml {
            body {
                div(classes = "border-2 border-red-500 bg-red-950/80 p-4 font-mono text-xs text-red-400 uppercase tracking-widest") {
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
                    val formattedDailyLocal = result.budget.dailyLocal?.let {
                        String.format(Locale("id", "ID"), "%,.0f", it)
                    } ?: "N/A"

                    val formattedDailyBudget = if (result.budget.localCurrency.equals("USD", ignoreCase = true)) {
                        "$${result.budget.dailyUsd} USD / hari"
                    } else {
                        "$${result.budget.dailyUsd} USD (~$formattedDailyLocal ${result.budget.localCurrency}) / hari"
                    }

                    val formattedActivities = if (result.tips.isNotEmpty()) {
                        result.tips.joinToString(". ")
                    } else if (result.places.isNotEmpty()) {
                        "Kunjungi: " + result.places.take(3).joinToString(", ") { it.name }
                    } else {
                        "Eksplorasi tempat wisata lokal dan nikmati kuliner khas setempat."
                    }

                    val viewPoiList = result.places.map { place ->
                        PointOfInterest(
                            name = place.name,
                            category = place.category ?: "ATTRACTION",
                            osmLink = place.mapUrl ?: "#",
                            lat = place.lat ?: 0.0,
                            lon = place.lon ?: 0.0
                        )
                    }

                    // PAKAI ANGKA DARI TRAVELSERVICE (TIDAK ADA MULTIPLIKASI GANDA)
                    val localTotal = result.budget.dailyLocal ?: 0.0
                    val idrTotal = result.budget.dailyIdr ?: 0.0

                    val locAcc = String.format(Locale("id", "ID"), "%,.0f", localTotal * 0.40)
                    val locFood = String.format(Locale("id", "ID"), "%,.0f", localTotal * 0.30)
                    val locTrans = String.format(Locale("id", "ID"), "%,.0f", localTotal * 0.15)

                    val idrAcc = String.format(Locale("id", "ID"), "%,.0f", idrTotal * 0.40)
                    val idrFood = String.format(Locale("id", "ID"), "%,.0f", idrTotal * 0.30)
                    val idrTrans = String.format(Locale("id", "ID"), "%,.0f", idrTotal * 0.15)

                    val totLocStr = String.format(Locale("id", "ID"), "%,.0f", localTotal)
                    val totIdrStr = String.format(Locale("id", "ID"), "%,.0f", idrTotal)

                    weatherWidget(
                        targetCity = result.targetCity,
                        population = String.format(Locale.US, "%,d", result.country.population),
                        recommendedActivity = formattedActivities,
                        seasonName = result.seasonName, // MENAMPILKAN MUSIM NYATA
                        seasonDesc = "Engine: ${result.recommenderLabel} | Est. Cost: $formattedDailyBudget",

                        countryName = result.country.name,
                        capital = result.country.capital,
                        destinationDescription = "Analisis destinasi otomatis berbasis telemetri cuaca dan lokasi.",
                        imageUrl = result.bannerUrl.ifBlank {
                            "https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80"
                        },

                        temperature = result.weather.temperature.toString(),
                        humidity = result.weather.humidity.toString(),
                        windSpeed = (result.weather.windSpeed ?: 12.0).toString(),
                        weatherDesc = result.weather.description,
                        isOutdoor = !result.isIndoor,

                        currencyCode = result.budget.localCurrency,
                        tierSelected = budgetTier.name,
                        localCostAcc = locAcc,
                        localCostFood = locFood,
                        localCostTrans = locTrans,
                        idrCostAcc = idrAcc,
                        idrCostFood = idrFood,
                        idrCostTrans = idrTrans,
                        totalLocalString = totLocStr,
                        totalIdrString = totIdrStr,

                        mapLat = result.weather.lat ?: 0.0,
                        mapLon = result.weather.lon ?: 0.0,
                        poiList = viewPoiList
                    )
                }
            }
        }
        is TravelPlanResult.Error -> {
            respondHtml {
                body {
                    div(classes = "border-2 border-amber-500 bg-amber-950/80 p-4 font-mono text-xs text-amber-400 uppercase tracking-widest") {
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