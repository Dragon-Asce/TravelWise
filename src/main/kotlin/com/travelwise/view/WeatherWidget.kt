// WeatherWidget.kt
package com.travelwise.view

import kotlinx.html.*

// Data Class dummy untuk POI OSM
data class PointOfInterest(
    val name: String,
    val category: String,
    val osmLink: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

fun FlowContent.weatherWidget(
    // Parameter Lama yang Dikembalikan
    targetCity: String,
    population: String,
    recommendedActivity: String,
    seasonName: String, 
    seasonDesc: String, 
    
    // Parameter Standar
    countryName: String,
    capital: String,
    destinationDescription: String,
    imageUrl: String,
    
    // Weather
    temperature: String, 
    humidity: String,    
    windSpeed: String,   
    weatherDesc: String,
    isOutdoor: Boolean,
    
    // Currency & Cost
    currencyCode: String,
    tierSelected: String, 
    localCostAcc: String,
    localCostFood: String,
    localCostTrans: String,
    idrCostAcc: String,
    idrCostFood: String,
    idrCostTrans: String,
    totalLocalString: String,
    totalIdrString: String,
    
    // Map & POI
    mapLat: Double = 35.6762,
    mapLon: Double = 139.6503,
    poiList: List<PointOfInterest>
) {
    
    val sym = if (currencyCode == "JPY") "¥" else if (currencyCode == "USD") "$" else if (currencyCode == "EUR") "€" else "Rp"

    section("pb-12 animate-enter") {
        id = "result-view"
        
        // --- HEADER BAR KECIL ---
        div("mb-4 pb-2 swiss-border-b flex justify-between items-end") {
            span("text-xs font-bold uppercase text-[#E61919]") { +"SYS. MSG: DATA SUCCESSFULLY COMPILED" }
            button(type = ButtonType.button) {
                id = "btn-reset-view"
                classes = setOf("text-xs", "font-bold", "uppercase", "text-gray-500", "hover:text-[#050505]", "transition-colors", "cursor-pointer")
                attributes["onclick"] = "document.getElementById('result-view').remove(); document.getElementById('search-view').classList.remove('hidden');"
                +"[ NEW QUERY ]"
            }
        }

        // --- TITLE & FOTO BESAR ---
        div("grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8 mb-8") {
            div("lg:col-span-7 flex flex-col justify-end") {
                h2("text-macro text-[#050505] break-words") {
                    +targetCity // Menggunakan targetCity yang dikembalikan
                    br()
                    span("text-[#E61919]") { +countryName }
                }
                p("text-base font-medium mt-4 max-w-xl") {
                    +destinationDescription
                }
            }
            
            div("lg:col-span-5 img-wrap bg-white p-2 swiss-border shadow-[8px_8px_0px_0px_#050505]") {
                img(src = imageUrl, alt = targetCity) {
                    classes = setOf("w-full", "h-64", "object-cover", "img-swiss")
                }
                div("text-[10px] font-bold uppercase mt-2 text-right text-gray-500") { +"IMG.SRC: UNSPLASH API" }
            }
        }

        // --- GRID KONTEN TENGAH ---
        div("grid grid-cols-1 md:grid-cols-12 gap-8 swiss-border-t pt-8") {
            
            // KIRI: Cuaca & Map
            div("md:col-span-5 space-y-6 flex flex-col") {
                
                // Weather Data
                div("bg-[#050505] text-white p-4") {
                    span("text-[10px] font-bold uppercase tracking-widest text-gray-400 block mb-2") { +"REAL-TIME WEATHER (OPENWEATHER)" }
                    div("flex items-end gap-2") {
                        span("text-6xl font-black tracking-tighter") { +"$temperature°" }
                        span("text-2xl font-bold mb-1") { +"C" }
                    }
                    span("text-sm font-bold uppercase mt-1 block text-[#E61919]") { +weatherDesc }
                    div("flex gap-4 text-xs font-bold uppercase mt-4 text-gray-400") {
                        span { +"HUMIDITY: $humidity%" }
                        span { +"WIND: $windSpeed KM/H" }
                    }
                }

                // Adaptive Directive (Menggunakan Recommended Activity)
                val directiveShadow = if (!isOutdoor) "shadow-[4px_4px_0px_0px_#E61919]" else "shadow-[4px_4px_0px_0px_#050505]"
                
                div("swiss-border p-4 bg-white $directiveShadow") {
                    span("text-[10px] font-bold uppercase tracking-widest text-[#E61919] block mb-1") { +"ADAPTIVE SYSTEM DIRECTIVE" }
                    p("text-lg font-black uppercase leading-tight") {
                        +recommendedActivity // Menggunakan rekomendasi aktivitas dari backend
                    }
                }

                // Data Demografi (Menggunakan Population & Season)
                div("swiss-border p-4 bg-[#E61919] text-white") {
                    div("grid grid-cols-2 gap-4 text-xs font-bold uppercase") {
                        div {
                            span("text-white/60 block mb-1") { +"TOTAL POPULATION" }
                            span("text-lg") { +"$population UNIT" } // Menggunakan populasi
                        }
                        div {
                            span("text-white/60 block mb-1") { +"CURRENT SEASON" }
                            span("text-lg") { +seasonName } // Menggunakan Season Name
                        }
                    }
                    p("mt-4 pt-2 border-t-2 border-white/20 text-[10px] leading-relaxed") {
                        +seasonDesc // Menggunakan Season Desc
                    }
                }

                // Monocrome Map (Leaflet)
                div("swiss-border p-2 bg-white flex-grow mt-2 relative min-h-[250px]") {
                    div("text-[10px] font-bold uppercase tracking-widest text-gray-500 block mb-2 px-2") { +"RADAR TELEMETRY (OPENSTREETMAP)" }
                    div("w-full h-[220px] bg-gray-200") {
                        id = "leaflet-map"
                    }
                }
            }

            // KANAN: Tabel Anggaran & POI List
            div("md:col-span-7 space-y-8") {
                
                // 1. Table Anggaran
                div {
                    div("flex justify-between items-end mb-2") {
                        span("text-sm font-bold uppercase text-gray-500") { +"FINANCIAL LOGISTICS (DAILY EST)" }
                        span("bg-[#E61919] text-white px-2 py-1 text-[10px] font-black uppercase") { +"TIER: $tierSelected" }
                    }
                    
                    // PERBAIKAN HTML DSL UNTUK TH
                    table("w-full text-left font-medium text-sm swiss-border bg-white") {
                        thead("bg-[#050505] text-white text-xs font-bold uppercase") {
                            tr {
                                th(classes = "p-3 swiss-border-r") { +"KATEGORI" }
                                th(classes = "p-3 swiss-border-r") { +"LOKAL ($currencyCode)" }
                                th(classes = "p-3") { +"KONVERSI (IDR)" }
                            }
                        }
                        tbody {
                            tr("swiss-border-t") {
                                td("p-3 swiss-border-r font-bold") { +"AKOMODASI" }
                                td("p-3 swiss-border-r") { +"$sym $localCostAcc" }
                                td("p-3 text-gray-500") { +"Rp $idrCostAcc" }
                            }
                            tr("swiss-border-t") {
                                td("p-3 swiss-border-r font-bold") { +"KONSUMSI" }
                                td("p-3 swiss-border-r") { +"$sym $localCostFood" }
                                td("p-3 text-gray-500") { +"Rp $idrCostFood" }
                            }
                            tr("swiss-border-t") {
                                td("p-3 swiss-border-r font-bold") { +"TRANSPORTASI" }
                                td("p-3 swiss-border-r") { +"$sym $localCostTrans" }
                                td("p-3 text-gray-500") { +"Rp $idrCostTrans" }
                            }
                            tr("swiss-border-t bg-gray-100") {
                                td("p-3 swiss-border-r font-black") { +"TOTAL ESTIMASI" }
                                td("p-3 swiss-border-r font-black text-[#E61919]") { +"$sym $totalLocalString" }
                                td("p-3 font-black text-[#E61919]") { +"Rp $totalIdrString" }
                            }
                        }
                    }
                }

                // 2. POI Cards (Osm)
                div {
                    div("flex justify-between items-end swiss-border-b pb-2 mb-4") {
                        span("text-sm font-bold uppercase") { +"ADAPTIVE POI INDEX (OSM OVERPASS)" }
                        span("text-[10px] font-bold text-[#E61919] uppercase") { if(isOutdoor) +"RECOMMENDED OUTDOOR" else +"RECOMMENDED INDOOR" }
                    }
                    
                    div("grid grid-cols-1 sm:grid-cols-2 gap-4") {
                        poiList.forEach { poi ->
                            a(href = poi.osmLink) {
                                classes = setOf("block", "bg-white", "p-4", "swiss-border", "hover:bg-[#050505]", "hover:text-white", "transition-colors", "group", "cursor-pointer")
                                target = "_blank"
                                span("text-[10px] font-bold uppercase text-gray-500 group-hover:text-gray-400 mb-2 block") { +"[ ${poi.category} ]" }
                                span("text-sm font-black uppercase leading-tight block mb-4") { +poi.name }
                                div("border-t-2 border-[#050505] group-hover:border-gray-600 pt-2 flex justify-between items-center text-xs font-bold uppercase") {
                                    span { +"OSM MAP" }
                                    span("transform group-hover:translate-x-1 group-hover:-translate-y-1 transition-transform") { +"↗" }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Script untuk inisialisasi Map Monokrom
        script {
            unsafe {
                raw("""
                    // Jalankan Inisialisasi Map Setelah View Dimuat
                    setTimeout(function() {
                        var map = L.map('leaflet-map', {
                            zoomControl: false,
                            attributionControl: false
                        }).setView([$mapLat, $mapLon], 13);
                        
                        // CartoDB Positron atau Stamen Toner (Monokrom style)
                        L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
                            maxZoom: 19
                        }).addTo(map);
                        
                        // Custom Marker Merah Swiss Brutal
                        var brutalIcon = L.divIcon({
                            className: 'custom-div-icon',
                            html: "<div style='background-color:#E61919; width:12px; height:12px; border:2px solid #050505;'></div>",
                            iconSize: [12, 12],
                            iconAnchor: [6, 6]
                        });
                        
                        // Add marker center
                        L.marker([$mapLat, $mapLon], {icon: brutalIcon}).addTo(map);
                    }, 100);
                """)
            }
        }
    }
}

// ------------------------------------------------------------------------
// Error Widget State (Brutalist 404)
// ------------------------------------------------------------------------
fun FlowContent.weatherErrorWidget(errorMessage: String) {
    section("bg-[#E61919] text-[#050505] p-8 md:p-16 text-center animate-enter relative overflow-hidden min-h-[60vh] flex flex-col justify-center") {
        id = "result-view"
        
        // Tanda silang (crosshairs) di background
        div("absolute inset-0 pointer-events-none opacity-20") {
            div("absolute top-8 left-8 w-8 h-px bg-[#050505]") {}
            div("absolute top-4 left-12 w-px h-8 bg-[#050505]") {}
            
            div("absolute bottom-8 right-8 w-8 h-px bg-[#050505]") {}
            div("absolute bottom-4 right-12 w-px h-8 bg-[#050505]") {}
        }
        
        h2("text-[clamp(4rem,15vw,12rem)] font-black uppercase tracking-tighter leading-none mix-blend-multiply opacity-50") {
            +"404"
        }
        h3("text-4xl md:text-6xl font-black uppercase tracking-tighter mt-[-2rem] relative z-10") {
            +"TARGET UNKNOWN"
        }
        
        p("text-base md:text-xl font-bold uppercase mt-8 max-w-xl mx-auto bg-[#050505] text-white p-4") {
            +errorMessage
        }
        
        div("mt-12") {
            button(type = ButtonType.button) {
                classes = setOf("bg-transparent", "hover:bg-[#050505]", "text-[#050505]", "hover:text-white", "font-black", "uppercase", "text-xl", "px-12", "py-4", "border-4", "border-[#050505]", "transition-colors", "cursor-pointer")
                attributes["onclick"] = "document.getElementById('result-view').remove(); document.getElementById('search-view').classList.remove('hidden');"
                +"RE-CALIBRATE ↺"
            }
        }
    }
}