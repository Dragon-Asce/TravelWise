package com.travelwise.view

import com.travelwise.model.Country
import com.travelwise.model.WeatherData
import kotlinx.html.*

object WeatherWidget {
    fun renderSuccess(
        html: HTML,
        country: Country,
        weather: WeatherData,
        recommendations: List<String>,
        dailyBudget: String = "$50 USD"
    ) {
        html.body {
            weatherWidget(
                countryName = country.name,
                capital = country.capital,
                population = String.format(java.util.Locale.US, "%,d", country.population),
                currencyCode = country.currencyCode,
                temperature = weather.temperature,
                humidity = weather.humidity,
                weatherDesc = "${weather.condition} (${weather.description})",
                recommendedActivity = recommendations.joinToString(" • "),
                dailyBudget = dailyBudget
            )
        }
    }

    fun renderError(html: HTML, message: String) {
        html.body {
            div("bg-red-100 border border-red-400 text-red-700 px-6 py-4 rounded-xl mt-6 shadow-sm") {
                p("font-bold text-lg mb-1") { +"⚠️ Informasi" }
                p { +message }
            }
        }
    }
}

// Parameter disesuaikan dengan data class yang dibuat tim backend (Country, WeatherData, dll)
fun FlowContent.weatherWidget(
    countryName: String,
    capital: String,
    population: String,
    currencyCode: String,
    temperature: Double,
    humidity: Int,
    weatherDesc: String,
    recommendedActivity: String,
    dailyBudget: String
) {
    div("grid grid-cols-1 md:grid-cols-2 gap-6 mt-6 animate-fade-in-up") {
        
        // 1. Kartu Profil Negara
        div("bg-white p-6 rounded-xl shadow-md border-l-4 border-blue-500") {
            h3("text-lg font-bold text-slate-800 mb-4 flex items-center gap-2") {
                +"🌍 Profil Tujuan"
            }
            ul("space-y-3 text-slate-600") {
                li { b("text-slate-700") { +"Negara:" }; +" $countryName" }
                li { b("text-slate-700") { +"Ibu Kota:" }; +" $capital" }
                li { b("text-slate-700") { +"Populasi:" }; +" $population jiwa" }
                li { b("text-slate-700") { +"Mata Uang:" }; +" $currencyCode" }
            }
        }

        // 2. Kartu Analisis Cuaca Real-Time
        div("bg-white p-6 rounded-xl shadow-md border-l-4 border-amber-400") {
            h3("text-lg font-bold text-slate-800 mb-4 flex items-center gap-2") {
                +"⛅ Kondisi Cuaca"
            }
            div("flex items-end gap-2 mb-2") {
                div("text-5xl font-extrabold text-slate-800") { +"$temperature°C" }
                div("text-slate-500 pb-1 font-medium capitalize") { +"($weatherDesc)" }
            }
            div("w-full bg-slate-200 rounded-full h-2.5 mt-4") {
                div("bg-amber-400 h-2.5 rounded-full") {
                    style = "width: ${humidity.coerceIn(0, 100)}%"
                }
            }
            p("text-slate-500 text-sm mt-1") { +"Tingkat Kelembapan: $humidity%" }
        }

        // 3. Kartu Rekomendasi Aktivitas
        div("bg-white p-6 rounded-xl shadow-md border-l-4 border-green-500") {
            h3("text-lg font-bold text-slate-800 mb-4 flex items-center gap-2") {
                +"🎯 Rekomendasi Otomatis"
            }
            p("text-slate-600 text-sm mb-3") {
                +"Sistem merekomendasikan aktivitas berikut berdasarkan cuaca saat ini:"
            }
            div("p-4 bg-green-50 text-green-800 font-semibold rounded-lg border border-green-100") {
                +"$recommendedActivity"
            }
        }

        // 4. Kartu Estimasi Anggaran
        div("bg-white p-6 rounded-xl shadow-md border-l-4 border-purple-500") {
            h3("text-lg font-bold text-slate-800 mb-4 flex items-center gap-2") {
                +"💸 Estimasi Anggaran Harian"
            }
            p("text-slate-600 text-sm mb-2") { +"Perkiraan biaya standar per hari:" }
            div("text-3xl font-bold text-purple-600") {
                +dailyBudget
            }
            p("text-xs text-slate-400 mt-3") { +"*Disesuaikan dengan nilai tukar mata uang lokal ($currencyCode)" }
        }
    }
}