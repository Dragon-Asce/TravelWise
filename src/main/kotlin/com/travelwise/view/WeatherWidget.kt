package com.travelwise.view

import com.travelwise.model.BudgetPlan
import com.travelwise.model.Place
import com.travelwise.service.TravelPlanResult
import java.util.Locale
import kotlinx.html.*

private fun money(v: Double): String =
    if (v >= 100) String.format(Locale.US, "%,.0f", v) else String.format(Locale.US, "%,.2f", v)

private fun categoryLabel(c: String) = when (c) {
    "museum" -> "Museum"
    "gallery" -> "Galeri"
    "cafe" -> "Kafe"
    "attraction" -> "Atraksi"
    "park" -> "Taman"
    "historic" -> "Bersejarah"
    else -> c.replaceFirstChar { it.uppercase() }
}

/** Dashboard hasil: banner, profil negara, cuaca, grid tempat, kalkulator budget. */
fun FlowContent.weatherWidget(result: TravelPlanResult.Success) {
    val country = result.country
    val weather = result.weather

    div("space-y-6 animate-slide-up") {

        // Banner kota
        section("relative overflow-hidden rounded-3xl h-56 border border-white/10") {
            img(alt = "Foto ${weather.cityName}", src = result.bannerUrl,
                classes = "absolute inset-0 w-full h-full object-cover")
            div("absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/50 to-transparent")
            div("absolute bottom-0 p-6") {
                h2("text-3xl font-bold") { +country.name }
                p("text-slate-300") { +weather.cityName }
            }
        }

        div("grid grid-cols-1 md:grid-cols-2 gap-6") {

            // Profil destinasi
            div("rounded-3xl border border-white/10 bg-white/5 p-6 space-y-3") {
                div("flex items-center gap-3") {
                    country.flagUrl?.let {
                        img(alt = "Bendera ${country.name}", src = it, classes = "h-8 rounded shadow")
                    }
                    h3("text-lg font-semibold") { +"Profil Destinasi" }
                }
                infoRow("Nama resmi", country.officialName)
                infoRow("Ibu kota", country.capital)
                infoRow("Populasi", if (country.population > 0) String.format(Locale.US, "%,d", country.population) else "N/A")
                infoRow("Mata uang", "${country.currencyName} (${country.currencyCode})")
            }

            // Cuaca real-time
            div("rounded-3xl border border-white/10 bg-white/5 p-6 space-y-3") {
                h3("text-lg font-semibold") { +"Cuaca Real-Time" }
                div("text-5xl font-extrabold") { +String.format(Locale.US, "%.1f\u00B0C", weather.temperature) }
                p("capitalize text-slate-300") { +weather.description }
                infoRow("Kelembapan", "${weather.humidity}%")
                infoRow("Angin", String.format(Locale.US, "%.1f m/s", weather.windSpeed))
                span("inline-block mt-1 rounded-full bg-sky-500/15 text-sky-300 text-xs px-3 py-1") {
                    +result.recommenderLabel
                }
            }
        }

        // Saran aktivitas
        div("rounded-3xl border border-white/10 bg-white/5 p-6") {
            h3("text-lg font-semibold mb-3") { +"Saran Aktivitas" }
            ul("space-y-2 text-slate-300 list-disc list-inside") {
                result.tips.forEach { li { +it } }
            }
        }

        // Grid tempat
        div {
            h3("text-lg font-semibold mb-3") { +"Tempat Rekomendasi" }
            if (result.places.isEmpty()) {
                div("rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-slate-400") {
                    +"Belum ada tempat spesifik yang ditemukan di OpenStreetMap untuk kota ini. Coba lagi beberapa saat (Overpass bisa sibuk)."
                }
            } else {
                div("grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4") {
                    result.places.forEach { placeCard(it) }
                }
            }
        }

        budgetCalculator(result.budget)
    }
}

private fun FlowContent.infoRow(label: String, value: String) {
    div("flex justify-between gap-4 text-sm border-b border-white/5 pb-2") {
        span("text-slate-400") { +label }
        span("text-right font-medium") { +value }
    }
}

private fun FlowContent.placeCard(place: Place) {
    div("overflow-hidden rounded-2xl border border-white/10 bg-white/5") {
        img(alt = place.name, src = place.imageUrl ?: "", classes = "h-36 w-full object-cover bg-slate-800")
        div("p-4 space-y-2") {
            span("inline-block rounded-full bg-indigo-500/20 text-indigo-300 text-xs px-3 py-1") {
                +categoryLabel(place.category)
            }
            h4("font-semibold leading-snug") { +place.name }
            place.mapUrl?.let { url ->
                a(href = url, target = "_blank", classes = "inline-block text-sm text-sky-400 hover:underline") {
                    attributes["rel"] = "noopener noreferrer"
                    +"Buka di Peta \u2192"
                }
            }
        }
    }
}

private fun FlowContent.budgetCalculator(budget: BudgetPlan) {
    div("rounded-3xl border border-white/10 bg-white/5 p-6 space-y-4") {
        div {
            h3("text-lg font-semibold") { +"Estimasi Budget Harian - ${budget.tier.label}" }
            p("text-sm text-slate-400") { +budget.tier.description }
        }

        div("grid grid-cols-1 sm:grid-cols-3 gap-3") {
            statBox("USD", "$${money(budget.dailyUsd)}")
            statBox(budget.localCurrency, budget.dailyLocal?.let { money(it) } ?: "Kurs tidak tersedia")
            statBox("IDR", budget.dailyIdr?.let { "Rp ${money(it)}" } ?: "Kurs tidak tersedia")
        }

        div("space-y-2") {
            budget.breakdown.forEach { line ->
                div("flex justify-between text-sm") {
                    span("text-slate-400") { +line.label }
                    span {
                        +"$${money(line.usd)}"
                        line.local?.let { +"  (~${money(it)} ${budget.localCurrency})" }
                    }
                }
            }
        }

        budget.localPerUsd?.let {
            p("text-xs text-slate-500") {
                +"Kurs: 1 USD = ${money(it)} ${budget.localCurrency} (open.er-api.com)"
            }
        }
    }
}

private fun FlowContent.statBox(label: String, value: String) {
    div("rounded-2xl bg-slate-900/60 border border-white/10 p-4") {
        div("text-xs text-slate-400") { +label }
        div("text-xl font-bold mt-1 break-words") { +value }
    }
}