package com.travelwise.view

import kotlinx.html.*

object HomeView {
    fun render(html: HTML) {
        html.homeView()
    }
}

fun HTML.homeView() {
    mainLayout {
        div("max-w-4xl mx-auto") {
            // Header Section
            div("text-center mb-10 mt-8") {
                h1("text-4xl md:text-5xl font-extrabold text-blue-700 mb-4") { 
                    +"Rencanakan Perjalananmu Lebih Cerdas" 
                }
                p("text-slate-600 text-lg") { 
                    +"Masukkan negara tujuan untuk mendapatkan analisis cuaca, rekomendasi aktivitas, dan estimasi anggaran." 
                }
            }

            // Search Form Widget
            div("bg-white p-6 md:p-8 rounded-xl shadow-lg border border-slate-100 mb-8") {
                form {
                    // --- HTMX Attributes Setup ---
                    attributes["hx-post"] = "/search" // Endpoint backend yang akan di-hit
                    attributes["hx-target"] = "#widget-container" // Target elemen ID untuk meletakkan hasil
                    attributes["hx-indicator"] = "#loading-spinner" // ID elemen yang tampil saat proses loading
                    attributes["hx-swap"] = "innerHTML"

                    div("flex flex-col md:flex-row gap-4") {
                        input(InputType.text, name = "country") {
                            classes = setOf("flex-1", "border", "border-slate-300", "rounded-lg", "p-4", "text-lg", "focus:outline-none", "focus:ring-2", "focus:ring-blue-500", "focus:border-transparent")
                            placeholder = "Contoh: Japan, Indonesia, France, Italy..."
                            required = true
                        }
                        button(type = ButtonType.submit) {
                            classes = setOf("bg-blue-600", "hover:bg-blue-700", "text-white", "font-bold", "py-4", "px-8", "rounded-lg", "transition", "duration-300", "flex", "items-center", "justify-center")
                            +"Cari Sekarang 🔍"
                        }
                    }
                }
            }

            // Loading State (Diatur oleh HTMX class indicator)
            div("text-center my-8 htmx-indicator") {
                id = "loading-spinner"
                div("animate-spin inline-block w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full")
                p("text-slate-500 mt-3 font-medium") { +"Menganalisis data real-time..." }
            }

            // Container untuk hasil render dari WeatherWidget.kt
            div {
                id = "widget-container"
            }
        }
    }
}