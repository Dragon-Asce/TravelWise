package com.travelwise.view

import kotlinx.html.*

data class PointOfInterest(
    val name: String,
    val category: String,
    val osmLink: String,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

fun FlowContent.weatherWidget(
    targetCity: String,
    population: String,
    recommendedActivity: String,
    seasonName: String,
    seasonDesc: String,

    countryName: String,
    capital: String,
    destinationDescription: String,
    imageUrl: String,

    temperature: String,
    humidity: String,
    windSpeed: String,
    weatherDesc: String,
    isOutdoor: Boolean,

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

    mapLat: Double = 0.0,
    mapLon: Double = 0.0,
    poiList: List<PointOfInterest>,
    localCostAct: String = "",
    idrCostAct: String = ""
) {

    val sym = when (currencyCode.uppercase()) {
        "JPY", "CNY" -> "¥"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "IDR" -> "Rp "
        else -> "$currencyCode "
    }

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
                    +targetCity
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

                // Adaptive Directive
                val directiveShadow = if (!isOutdoor) "shadow-[4px_4px_0px_0px_#E61919]" else "shadow-[4px_4px_0px_0px_#050505]"

                div("swiss-border p-4 bg-white $directiveShadow") {
                    span("text-[10px] font-bold uppercase tracking-widest text-[#E61919] block mb-1") { +"ADAPTIVE SYSTEM DIRECTIVE" }
                    p("text-lg font-black uppercase leading-tight") {
                        +recommendedActivity
                    }
                }

                // Data Demografi
                div("swiss-border p-4 bg-[#E61919] text-white") {
                    div("grid grid-cols-2 gap-4 text-xs font-bold uppercase") {
                        div {
                            span("text-white/60 block mb-1") { +"TOTAL POPULATION" }
                            span("text-lg") { +"$population UNIT" }
                        }
                        div {
                            span("text-white/60 block mb-1") { +"CURRENT SEASON" }
                            span("text-lg") { +seasonName }
                        }
                    }
                    p("mt-4 pt-2 border-t-2 border-white/20 text-[10px] leading-relaxed") {
                        +seasonDesc
                    }
                }

                // Radar Telemetry (Leaflet Map Container)
                div("swiss-border p-2 bg-white flex-grow mt-2 relative min-h-[250px]") {
                    div("text-[10px] font-bold uppercase tracking-widest text-gray-500 block mb-2 px-2") { +"RADAR TELEMETRY (OPENSTREETMAP)" }
                    div("w-full h-[220px] bg-[#111111] z-0") {
                        id = "leaflet-map"
                        attributes["style"] = "height: 220px; width: 100%;"
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
                                td("p-3 swiss-border-r") { +"$sym$localCostAcc" }
                                td("p-3 text-gray-500") { +"Rp $idrCostAcc" }
                            }
                            tr("swiss-border-t") {
                                td("p-3 swiss-border-r font-bold") { +"KONSUMSI" }
                                td("p-3 swiss-border-r") { +"$sym$localCostFood" }
                                td("p-3 text-gray-500") { +"Rp $idrCostFood" }
                            }
                            tr("swiss-border-t") {
                                td("p-3 swiss-border-r font-bold") { +"TRANSPORTASI" }
                                td("p-3 swiss-border-r") { +"$sym$localCostTrans" }
                                td("p-3 text-gray-500") { +"Rp $idrCostTrans" }
                            }
                            if (localCostAct.isNotEmpty()) {
                                tr("swiss-border-t") {
                                    td("p-3 swiss-border-r font-bold") { +"AKTIVITAS & TIKET" }
                                    td("p-3 swiss-border-r") { +"$sym$localCostAct" }
                                    td("p-3 text-gray-500") { +"Rp $idrCostAct" }
                                }
                            }
                            tr("swiss-border-t bg-gray-100") {
                                td("p-3 swiss-border-r font-black") { +"TOTAL ESTIMASI" }
                                td("p-3 swiss-border-r font-black text-[#E61919]") { +"$sym$totalLocalString" }
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

        // Script Inisialisasi Leaflet dengan API Key Stadia Maps
        script {
            unsafe {
                raw("""
                    setTimeout(function() {
                        var mapContainer = document.getElementById('leaflet-map');
                        if (!mapContainer) return;

                        // Bersihkan instance Leaflet lama secara aman
                        if (window._twLeafletMap) {
                            try {
                                window._twLeafletMap.remove();
                            } catch(e) {}
                            window._twLeafletMap = null;
                        }
                        if (mapContainer._leaflet_id) {
                            mapContainer._leaflet_id = null;
                            mapContainer.innerHTML = '';
                        }

                        var map = L.map('leaflet-map', {
                            zoomControl: false,
                            attributionControl: false
                        }).setView([$mapLat, $mapLon], 12);
                        window._twLeafletMap = map;
                        
                        // Menghubungkan API Key Stadia Maps ke Tile Layer Alidade Smooth Dark
                        var apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhIjoiYWNfY2owc280dnYiLCJqdGkiOiJkOWRmNjFiOTg4Mzg0ZGFhYWFkZmI3YmVhZjdhMWU0OSJ9._hBQjsEe8r4S3tAz5cKztdr1jpli1sc7J3ksiksLSro";
                        
                        L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png?api_key=' + apiKey, {
                            maxZoom: 19,
                            attribution: '© Stadia Maps, © OpenMapTiles © OpenStreetMap contributors'
                        }).addTo(map);
                        
                        // Marker Merah Swiss Brutalism Kontras
                        var brutalIcon = L.divIcon({
                            className: 'custom-div-icon',
                            html: "<div style='background-color:#E61919; width:14px; height:14px; border:2px solid #FFFFFF;'></div>",
                            iconSize: [14, 14],
                            iconAnchor: [7, 7]
                        });
                        
                        L.marker([$mapLat, $mapLon], {icon: brutalIcon}).addTo(map);
                        
                        // Memaksa render ulang dimensi peta setelah kontainer HTMX siap
                        setTimeout(function() { 
                            if (window._twLeafletMap) {
                                window._twLeafletMap.invalidateSize();
                            }
                        }, 200);
                    }, 50);
                """)
            }
        }
    }
}

fun FlowContent.weatherErrorWidget(errorMessage: String) {
    section("bg-[#E61919] text-[#050505] p-8 md:p-16 text-center animate-enter relative overflow-hidden min-h-[60vh] flex flex-col justify-center") {
        id = "result-view"

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