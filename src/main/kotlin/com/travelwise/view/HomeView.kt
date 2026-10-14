package com.travelwise.view

import kotlinx.html.*

fun HTML.homeView() {
    mainLayout {
        div("max-w-5xl mx-auto") {
            // Hero Section
            div("text-center mb-10 animate-slide-up") {
                div("inline-flex items-center gap-2 py-2 px-4 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 font-medium text-xs md:text-sm mb-6 backdrop-blur-md shadow-inner") {
                    span("text-base") { +"🧭" }
                    +"Smart AI & Weather Travel Planner"
                }
                h1("text-4xl md:text-6xl font-black text-white mb-6 leading-tight tracking-tight") { 
                    +"Jelajahi Keindahan Dunia, "
                    br()
                    span("bg-clip-text text-transparent bg-gradient-to-r from-sky-400 via-indigo-300 to-emerald-400 animate-pulse-glow") { 
                        +"Pahami Suasana & Cuaca." 
                    }
                }
                p("text-slate-400 text-base md:text-lg max-w-2xl mx-auto leading-relaxed") { 
                    +"Ketik kota atau negara impianmu untuk mendapatkan potret destinasi, cuaca real-time beranimasi, musim terkini, dan estimasi anggaran liburan." 
                }
            }

            // Glassmorphism Search Box
            div("group relative max-w-3xl mx-auto mb-6 animate-slide-up delay-100") {
                div("absolute -inset-1 bg-gradient-to-r from-sky-500 via-indigo-500 to-emerald-500 rounded-[2.5rem] blur-xl opacity-25 group-hover:opacity-60 transition duration-700 group-hover:duration-200")
                
                div("relative bg-slate-900/90 backdrop-blur-2xl p-3 md:p-4 rounded-[2.2rem] border border-slate-800 shadow-2xl") {
                    form {
                        attributes["hx-get"] = "/api/search"
                        attributes["hx-target"] = "#widget-container"
                        attributes["hx-indicator"] = "#loading-spinner"
                        attributes["hx-swap"] = "innerHTML"

                        div("flex flex-col md:flex-row gap-3 relative") {
                            div("relative flex-1 flex items-center") {
                                div("absolute left-5 text-xl text-slate-400 transition-transform duration-300 group-focus-within:scale-125 group-focus-within:text-sky-400") {
                                    +"🌏"
                                }
                                input(InputType.text, name = "destination") {
                                    id = "search-input"
                                    classes = setOf(
                                        "w-full", "pl-14", "pr-6", "py-4", "md:py-5", "bg-slate-950/60", "border", "border-slate-800", 
                                        "rounded-2xl", "text-base", "md:text-lg", "font-medium", "text-white", "placeholder-slate-500",
                                        "focus:outline-none", "focus:border-sky-500/80", "focus:ring-4", "focus:ring-sky-500/20",
                                        "transition-all", "duration-300"
                                    )
                                    placeholder = "Mau liburan ke mana? (Contoh: Tokyo, Paris, Bali, London...)"
                                    required = true
                                }
                            }
                            button(type = ButtonType.submit) {
                                classes = setOf(
                                    "bg-gradient-to-r", "from-sky-500", "via-indigo-600", "to-emerald-500",
                                    "hover:from-sky-400", "hover:to-emerald-400",
                                    "text-white", "font-bold", "text-base", "md:text-lg", "py-4", "px-8", "rounded-2xl",
                                    "shadow-lg", "shadow-indigo-500/25", "hover:shadow-sky-500/40",
                                    "active:scale-95", "transition-all", "duration-300",
                                    "flex", "items-center", "justify-center", "gap-2", "cursor-pointer"
                                )
                                span { +"Jelajahi" }
                                span("text-xl group-hover:translate-x-1 transition-transform duration-300") { +"🚀" }
                            }
                        }
                    }
                }
            }

            // Quick Search Destination Badges
            div("flex flex-wrap items-center justify-center gap-2.5 mb-12 animate-slide-up delay-200 text-xs font-medium text-slate-400") {
                span("text-slate-500 mr-1") { +"Destinasi Populer:" }
                val popularDestinations = listOf(
                    "🇯🇵 Tokyo" to "Tokyo",
                    "🇮🇩 Bali" to "Bali",
                    "🇨🇭 Zurich" to "Zurich",
                    "🇫🇷 Paris" to "Paris",
                    "🇬🇧 London" to "London",
                    "🇰🇷 Seoul" to "Seoul"
                )
                popularDestinations.forEach { (label, queryValue) ->
                    button(type = ButtonType.button) {
                        classes = setOf(
                            "px-3.5", "py-1.5", "rounded-full", "bg-slate-900/80", "border", "border-slate-800",
                            "hover:border-sky-500/60", "hover:text-white", "hover:bg-sky-500/10",
                            "active:scale-95", "transition-all", "duration-200", "cursor-pointer"
                        )
                        attributes["hx-get"] = "/api/search?destination=$queryValue"
                        attributes["hx-target"] = "#widget-container"
                        attributes["hx-indicator"] = "#loading-spinner"
                        attributes["hx-swap"] = "innerHTML"
                        +label
                    }
                }
            }

            // Skeleton Screen Loading
            div("htmx-indicator grid-cols-1 md:grid-cols-2 gap-6 my-8 animate-fade-in") {
                id = "loading-spinner"
                repeat(4) {
                    div("bg-slate-900/70 p-6 rounded-3xl border border-slate-800/80 animate-pulse space-y-4") {
                        div("flex items-center gap-3") {
                            div("w-12 h-12 rounded-2xl bg-slate-800/80")
                            div("space-y-2 flex-1") {
                                div("h-4 bg-slate-800/80 rounded w-1/3")
                                div("h-3 bg-slate-800/40 rounded w-1/4")
                            }
                        }
                        div("h-28 bg-slate-800/50 rounded-2xl w-full")
                        div("h-4 bg-slate-800/30 rounded w-2/3")
                    }
                }
            }

            // Target Container Hasil Search
            div("min-h-[200px]") {
                id = "widget-container"
            }
        }
    }
}