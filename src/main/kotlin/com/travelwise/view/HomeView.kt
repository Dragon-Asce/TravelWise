package com.travelwise.view

import kotlinx.html.*

<<<<<<< HEAD
/** Halaman utama: hero + form pencarian + pilihan tier budget (HTMX). */
fun HTML.homeView() = mainLayout {
    div {
        section("text-center mb-10") {
            h1("text-4xl sm:text-5xl font-extrabold bg-gradient-to-r from-sky-400 to-indigo-400 bg-clip-text text-transparent") {
                +"TravelWise"
            }
            p("mt-3 text-slate-400") {
                +"Profil negara, cuaca real-time, tempat wisata asli, dan estimasi budget harian."
=======
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
>>>>>>> d49ed39d1f2413234d71f425e96976e7e4d2b929
            }

<<<<<<< HEAD
        form(classes = "rounded-3xl border border-white/10 bg-white/5 backdrop-blur-md p-6 space-y-5") {
            attributes["hx-post"] = "/search"
            attributes["hx-target"] = "#result-container"
            attributes["hx-swap"] = "innerHTML"

            input(type = InputType.text, name = "country",
                classes = "w-full rounded-2xl bg-slate-900/70 border border-white/10 px-4 py-3 outline-none focus:border-sky-400") {
                placeholder = "Masukkan negara atau kota, mis. Japan, Jepang, Paris..."
                required = true
            }

            div("grid grid-cols-1 sm:grid-cols-3 gap-3") {
                BudgetTier.values().forEach { tier ->
                    label("cursor-pointer") {
                        input(type = InputType.radio, name = "tier", classes = "peer sr-only") {
                            value = tier.name
                            checked = tier == BudgetTier.MODERATE
                        }
                        div("rounded-2xl border border-white/10 bg-slate-900/60 p-4 transition peer-checked:border-sky-400 peer-checked:bg-sky-500/10") {
                            div("font-semibold") { +tier.label }
                            div("text-xs text-slate-400 mt-1") { +tier.description }
                        }
                    }
                }
            }

            button(type = ButtonType.submit,
                classes = "w-full rounded-2xl bg-gradient-to-r from-sky-500 to-indigo-500 py-3 font-semibold hover:opacity-90 transition") {
                +"Cari Rencana Perjalanan"
            }

            div("htmx-indicator items-center justify-center gap-2 text-sm text-slate-400") {
                +"Mengambil data negara, cuaca, tempat, dan kurs..."
            }
        }

        div("mt-8") { id = "result-container" }
=======
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
>>>>>>> d49ed39d1f2413234d71f425e96976e7e4d2b929
    }
}