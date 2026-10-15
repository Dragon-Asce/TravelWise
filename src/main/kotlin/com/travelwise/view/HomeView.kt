package com.travelwise.view

import kotlinx.html.*

fun HTML.homeView() {
    mainLayout {
        
        // =========================================================
        // VIEW 1: SEARCH & BUDGET FORM
        // =========================================================
        section("space-y-8 animate-enter") {
            id = "search-view"
            
            div {
                h2("text-macro mb-2") {
                    +"TARGET"
                    br()
                    +"ANALYSIS."
                }
            }

            form("max-w-4xl pt-4") {
                attributes["hx-get"] = "/api/search"
                attributes["hx-target"] = "#widget-container"
                attributes["hx-indicator"] = "#loading-view"
                attributes["hx-swap"] = "innerHTML"
                
                // Form di-hide setelah submit menggunakan HTMX
                attributes["hx-on::after-request"] = "if(event.detail.successful) document.getElementById('search-view').classList.add('hidden');"

                div("grid grid-cols-1 md:grid-cols-12 gap-4 items-end") {
                    // Destinasi Input
                    div("md:col-span-8 flex flex-col") {
                        label("text-xs font-bold uppercase text-gray-500 mb-2") { +"1. GEOGRAPHIC QUERY (CITY/COUNTRY)" }
                        input(InputType.text, name = "destination") {
                            id = "search-input"
                            placeholder = "E.G. TOKYO, PARIS, BALI"
                            required = true
                            classes = setOf(
                                "bg-white", "swiss-border", "p-4", "text-xl", "font-bold", "uppercase", 
                                "tracking-wider", "text-[#050505]", "focus:outline-none", "focus:ring-4", 
                                "focus:ring-[#E61919]/20", "focus:border-[#E61919]"
                            )
                        }
                    }

                    // Execute Button
                    div("md:col-span-4") {
                        button(type = ButtonType.submit) {
                            classes = setOf(
                                "w-full", "bg-[#E61919]", "hover:bg-[#050505]", "text-white", "font-black", 
                                "uppercase", "tracking-tighter", "text-xl", "p-4", "swiss-border", 
                                "transition-colors", "cursor-pointer"
                            )
                            +"EXECUTE ↘"
                        }
                    }
                }

                // Budget Tier Selector
                div("mt-8") {
                    label("text-xs font-bold uppercase text-gray-500 mb-2 block") { +"2. LOGISTICS TIER CALCULATION" }
                    div("grid grid-cols-3 gap-2") {
                        // Backpacker
                        div {
                            input(InputType.radio, name = "budget_tier") {
                                id = "tier-bp"
                                value = "backpacker"
                                classes = setOf("hidden")
                                checked = true
                            }
                            label("block text-center swiss-border p-3 text-sm font-bold uppercase cursor-pointer bg-white hover:bg-gray-100 transition-colors") {
                                htmlFor = "tier-bp"
                                +"BACKPACKER"
                            }
                        }
                        // Moderate
                        div {
                            input(InputType.radio, name = "budget_tier") {
                                id = "tier-mod"
                                value = "moderate"
                                classes = setOf("hidden")
                            }
                            label("block text-center swiss-border p-3 text-sm font-bold uppercase cursor-pointer bg-white hover:bg-gray-100 transition-colors") {
                                htmlFor = "tier-mod"
                                +"MODERATE"
                            }
                        }
                        // Luxury
                        div {
                            input(InputType.radio, name = "budget_tier") {
                                id = "tier-lux"
                                value = "luxury"
                                classes = setOf("hidden")
                            }
                            label("block text-center swiss-border p-3 text-sm font-bold uppercase cursor-pointer bg-white hover:bg-gray-100 transition-colors") {
                                htmlFor = "tier-lux"
                                +"LUXURY"
                            }
                        }
                    }
                }
            }
        }

        // =========================================================
        // VIEW 2: LOADING STATE (HTMX Indicator)
        // =========================================================
        section("htmx-indicator py-32 text-center w-full") {
            id = "loading-view"
            
            h2("text-4xl md:text-6xl font-black uppercase tracking-tighter text-[#050505]") {
                +"FETCHING DATA"
                span("animate-blink text-[#E61919]") { +"_" }
            }
            div("text-xs font-bold uppercase tracking-widest text-gray-500 mt-6 space-y-1") {
                p { +"> OPENWEATHERMAP API CONNECTING..." }
                p { +"> OVERPASS OSM EXTRACTING POI..." }
                p { +"> EXCHANGE-RATE CALCULATING IDR..." }
            }
        }

        // =========================================================
        // CONTAINER HASIL PENCARIAN (Target HTMX)
        // =========================================================
        div {
            id = "widget-container"
        }
    }
}