package com.travelwise.view

import kotlinx.html.*

fun HTML.homeView() {
    mainLayout {
        div("max-w-5xl mx-auto space-y-10") {
            
            // Hero Title Macro-Typography
            div("border-b-2 border-slate-800 pb-8 space-y-4") {
                div("inline-block px-3 py-1 bg-amber-400 text-black font-black text-xs uppercase tracking-widest border border-black shadow-[3px_3px_0px_0px_#ffffff]") {
                    +"// DESTINATION_INTELLIGENCE_MODULE"
                }
                h1("text-4xl md:text-7xl font-black font-heading text-white uppercase tracking-tighter leading-none") { 
                    +"ANALISIS WEATHER "
                    br()
                    span("text-amber-400 underline decoration-4 underline-offset-8") { +"& ESTIMASI BUDGET" }
                }
                p("text-slate-400 text-sm md:text-base font-mono max-w-2xl leading-relaxed") { 
                    +"> Masukkan parameter kota/negara tujuan untuk menarik profil geografis, telemetri cuaca real-time, status musim, serta kalkulasi budget harian."
                }
            }

            // Industrial Search Box
            div("bg-[#141820] border-2 border-slate-700 p-4 md:p-6 shadow-[8px_8px_0px_0px_#38bdf8] relative") {
                div("absolute -top-3 left-4 px-2 bg-[#0d0f12] text-sky-400 text-xs font-bold border border-sky-400 uppercase tracking-widest") {
                    +"[ INPUT_DESTINATION_QUERY ]"
                }

                form {
                    attributes["hx-get"] = "/api/search"
                    attributes["hx-target"] = "#widget-container"
                    attributes["hx-indicator"] = "#loading-spinner"
                    attributes["hx-swap"] = "innerHTML"

                    div("flex flex-col md:flex-row gap-3 mt-2") {
                        div("relative flex-1") {
                            input(InputType.text, name = "destination") {
                                id = "search-input"
                                classes = setOf(
                                    "w-full", "px-4", "py-4", "bg-black", "border-2", "border-slate-700",
                                    "text-white", "font-mono", "text-lg", "placeholder-slate-600",
                                    "focus:outline-none", "focus:border-amber-400", "focus:ring-0",
                                    "uppercase", "tracking-wider"
                                )
                                placeholder = "TOKYO, BALI, ZURICH, PARIS..."
                                required = true
                            }
                        }
                        button(type = ButtonType.submit) {
                            classes = setOf(
                                "bg-amber-400", "hover:bg-amber-300", "text-black", "font-black", "font-heading",
                                "text-lg", "px-8", "py-4", "border-2", "border-black",
                                "shadow-[4px_4px_0px_0px_#ffffff]", "active:translate-x-1", "active:translate-y-1", "active:shadow-none",
                                "transition-all", "cursor-pointer", "uppercase", "tracking-wider"
                            )
                            +"> EXECUTE_SEARCH"
                        }
                    }
                }

                // Quick Select Chips (Industrial Terminal Tags)
                div("mt-6 pt-4 border-t border-slate-800 flex flex-wrap items-center gap-2 text-xs font-mono") {
                    span("text-slate-500 font-bold uppercase") { +"[ QUICK_TARGETS ]:" }
                    val quickDestinations = listOf(
                        "TOKYO // JPN" to "Tokyo",
                        "BALI // IDN" to "Bali",
                        "ZURICH // CHE" to "Zurich",
                        "PARIS // FRA" to "Paris",
                        "LONDON // GBR" to "London"
                    )
                    quickDestinations.forEach { (label, queryValue) ->
                        button(type = ButtonType.button) {
                            classes = setOf(
                                "px-3", "py-1.5", "bg-slate-900", "hover:bg-amber-400", "hover:text-black",
                                "text-slate-300", "border", "border-slate-700", "font-bold", "uppercase",
                                "active:translate-x-0.5", "active:translate-y-0.5", "transition-all", "cursor-pointer"
                            )
                            attributes["hx-get"] = "/api/search?destination=$queryValue"
                            attributes["hx-target"] = "#widget-container"
                            attributes["hx-indicator"] = "#loading-spinner"
                            attributes["hx-swap"] = "innerHTML"
                            +label
                        }
                    }
                }
            }

            // Mechanical Terminal Buffer Loading Indicator
            div("htmx-indicator border-2 border-amber-400 bg-black p-6 my-8 text-center space-y-3 shadow-[6px_6px_0px_0px_#f59e0b]") {
                id = "loading-spinner"
                div("inline-block w-6 h-6 border-4 border-amber-400 border-t-transparent animate-spin")
                p("text-amber-400 font-mono text-sm font-bold uppercase tracking-widest animate-blink") {
                    +">> FETCHING_TELEMETRY_DATA_FROM_EXTERNAL_APIS..."
                }
            }

            // Target Container Hasil Search
            div {
                id = "widget-container"
            }
        }
    }
}