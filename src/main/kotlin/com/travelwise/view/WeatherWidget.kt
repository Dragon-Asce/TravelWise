package com.travelwise.view

import kotlinx.html.*

fun FlowContent.weatherWidget(
    countryName: String,
    capital: String,
    population: String,
    currencyCode: String,
    temperature: Double,
    humidity: Int,
    weatherDesc: String,
    seasonName: String,
    seasonDesc: String,
    destinationDescription: String,
    imageUrl: String,
    recommendedActivity: String,
    dailyBudget: String,
    isOutdoor: Boolean = true,
    targetCity: String = capital
) {
    div("space-y-6 animate-slide-up") {

        // ==========================================
        // 1. HERO DESTINATION FRAME (Monochrome/High-Contrast Blueprint)
        // ==========================================
        div("bg-[#12151a] border-2 border-slate-700 shadow-[8px_8px_0px_0px_#ffffff] relative overflow-hidden") {
            
            div("grid grid-cols-1 lg:grid-cols-12") {
                // Foto Banner dengan Frame Blueprint Industri
                div("lg:col-span-5 relative h-64 lg:h-auto border-b-2 lg:border-b-0 lg:border-r-2 border-slate-700 bg-black") {
                    img(src = imageUrl, alt = countryName, classes = "w-full h-full object-cover grayscale contrast-125 hover:grayscale-0 transition-all duration-500")
                    div("absolute top-2 left-2 bg-black px-2 py-1 border border-amber-400 text-amber-400 text-[10px] font-mono font-bold uppercase") {
                        +"[ IMG_REF // $targetCity ]"
                    }
                }

                // Metadata Teknis & Deskripsi
                div("lg:col-span-7 p-6 md:p-8 space-y-5 flex flex-col justify-between") {
                    div("space-y-3") {
                        div("flex items-center gap-2 flex-wrap text-xs font-mono") {
                            span("px-2 py-0.5 bg-amber-400 text-black font-black uppercase") { +"LOCATION_FOUND" }
                            span("px-2 py-0.5 bg-slate-800 text-slate-300 border border-slate-700 uppercase") { +"ISO_VAL: $currencyCode" }
                        }
                        h2("text-3xl md:text-5xl font-black font-heading text-white uppercase tracking-tight") {
                            +"$targetCity, $countryName"
                        }
                        p("text-slate-300 text-xs md:text-sm font-mono leading-relaxed bg-black/60 p-4 border border-slate-800") {
                            +"> $destinationDescription"
                        }
                    }

                    // Grid Data Telemetri Negara
                    div("grid grid-cols-2 gap-3 pt-4 border-t border-slate-800 text-xs font-mono") {
                        div("bg-black p-3 border border-slate-800") {
                            p("text-slate-500 font-bold uppercase") { +"[ CAPITAL_CITY ]" }
                            p("text-white font-black text-sm") { +capital }
                        }
                        div("bg-black p-3 border border-slate-800") {
                            p("text-slate-500 font-bold uppercase") { +"[ POPULATION ]" }
                            p("text-sky-400 font-black text-sm") { +"$population RECT" }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. BENTO TELEMETRY: WEATHER & SEASON MODULES
        // ==========================================
        div("grid grid-cols-1 md:grid-cols-3 gap-6") {

            // ------------------------------------------
            // MODULE 1: WEATHER TELEMETRY
            // ------------------------------------------
            val isRainy = weatherDesc.contains("rain", ignoreCase = true) || weatherDesc.contains("hujan", ignoreCase = true)
            val isSnowy = weatherDesc.contains("snow", ignoreCase = true) || weatherDesc.contains("salju", ignoreCase = true)

            div("bg-[#12151a] border-2 border-sky-400 p-6 shadow-[6px_6px_0px_0px_#38bdf8] relative flex flex-col justify-between") {
                div("absolute top-2 right-2 px-2 py-0.5 bg-sky-400 text-black font-black text-[10px] uppercase") {
                    +"SYS_MOD // 01"
                }

                div("space-y-4") {
                    h3("text-lg font-black font-heading text-white uppercase tracking-wider flex items-center gap-2") {
                        span("text-sky-400") { +"[⚡]" }
                        +"REALTIME_WEATHER"
                    }

                    div("bg-black p-4 border border-sky-500/40 text-center relative overflow-hidden") {
                        if (isRainy) {
                            div("absolute inset-0 opacity-30 flex justify-around pointer-events-none") {
                                repeat(6) { div("w-0.5 bg-sky-400 animate-rain-brutal") }
                            }
                        }
                        div("text-5xl font-black font-heading text-sky-400 tracking-tighter") {
                            +"$temperature°C"
                        }
                        p("text-slate-400 text-xs font-mono font-bold uppercase mt-1") {
                            +"STATUS: $weatherDesc"
                        }
                    }

                    div("space-y-1 text-xs font-mono") {
                        div("flex justify-between text-slate-400") {
                            span { +"HUMIDITY_LEVEL:" }
                            span("text-sky-400 font-bold") { +"$humidity%" }
                        }
                        div("w-full bg-black h-3 border border-slate-700 p-0.5") {
                            div("bg-sky-400 h-full") {
                                style = "width: $humidity%"
                            }
                        }
                    }
                }
            }

            // ------------------------------------------
            // MODULE 2: SEASONAL INTELLIGENCE
            // ------------------------------------------
            div("bg-[#12151a] border-2 border-amber-400 p-6 shadow-[6px_6px_0px_0px_#f59e0b] relative flex flex-col justify-between") {
                div("absolute top-2 right-2 px-2 py-0.5 bg-amber-400 text-black font-black text-[10px] uppercase") {
                    +"SYS_MOD // 02"
                }

                div("space-y-4") {
                    h3("text-lg font-black font-heading text-white uppercase tracking-wider flex items-center gap-2") {
                        span("text-amber-400") { +"[🌐]" }
                        +"SEASON_TELEMETRY"
                    }

                    div("bg-black p-4 border border-amber-500/40 space-y-2") {
                        div("inline-block px-2 py-0.5 bg-amber-400 text-black font-black text-xs uppercase") {
                            +seasonName
                        }
                        p("text-slate-300 text-xs font-mono leading-relaxed") {
                            +"> $seasonDesc"
                        }
                    }
                }

                p("text-[10px] text-slate-500 font-mono uppercase border-t border-slate-800 pt-3") {
                    +"[ GEAR_RECOMMENDATION: ADAPTIVE_TO_CURRENT_SEASON ]"
                }
            }

            // ------------------------------------------
            // MODULE 3: ACTION & BUDGET CALCULATOR
            // ------------------------------------------
            div("bg-[#12151a] border-2 border-emerald-400 p-6 shadow-[6px_6px_0px_0px_#10b981] relative flex flex-col justify-between md:col-span-3 lg:col-span-1") {
                div("absolute top-2 right-2 px-2 py-0.5 bg-emerald-400 text-black font-black text-[10px] uppercase") {
                    +"SYS_MOD // 03"
                }

                div("space-y-4") {
                    h3("text-lg font-black font-heading text-white uppercase tracking-wider flex items-center gap-2") {
                        span("text-emerald-400") { +"[🎯]" }
                        +"ACTION_&_BUDGET"
                    }

                    div("bg-black p-3 border border-emerald-500/40 space-y-2") {
                        span("inline-block px-2 py-0.5 bg-emerald-500/20 text-emerald-400 text-[10px] font-bold border border-emerald-500/40 uppercase") {
                            if (isOutdoor) +"[ MODE: OUTDOOR_ACTION ]" else +"[ MODE: INDOOR_ACTION ]"
                        }
                        p("text-emerald-200 text-xs font-mono leading-relaxed") {
                            +"> $recommendedActivity"
                        }
                    }

                    div("bg-black p-3 border border-slate-700 flex justify-between items-center") {
                        div {
                            p("text-[10px] text-slate-500 font-bold uppercase") { +"[ EST_DAILY_BUDGET ]" }
                            p("text-lg font-black text-emerald-400 font-heading") { +dailyBudget }
                        }
                        span("text-xl text-emerald-400 font-mono") { +"[VAL]" }
                    }
                }
            }
        }
    }
}

// Error Component Telemetri Industri
fun FlowContent.weatherErrorWidget(errorMessage: String) {
    div("p-6 bg-black border-2 border-red-500 shadow-[6px_6px_0px_0px_#ef4444] text-center max-w-lg mx-auto my-6 space-y-3 font-mono") {
        div("inline-block px-3 py-1 bg-red-500 text-black font-black text-xs uppercase") {
            +"[ ERROR_404 // TARGET_NOT_FOUND ]"
        }
        h4("text-lg font-black font-heading text-white uppercase") { +"FAILED TO RESOLVE DESTINATION" }
        p("text-slate-400 text-xs leading-relaxed") { +"> $errorMessage" }
        div("text-[10px] text-red-400 border border-red-500/40 p-2 bg-red-950/20 uppercase") {
            +"PLEASE CHECK SPELLING (E.G. TOKYO, BALI, ZURICH, PARIS)"
        }
    }
}