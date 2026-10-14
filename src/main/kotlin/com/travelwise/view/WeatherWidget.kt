package com.travelwise.view

import kotlinx.html.*

fun FlowContent.weatherWidget(
    countryName: String,
    capital: String,
    population: String,
    currencyCode: String,
    temperature: Double,
    humidity: Int,
    weatherDesc: String,             // Misal: "Rainy", "Sunny", "Snowy", "Cloudy"
    seasonName: String,              // Misal: "Musim Semi (Spring)", "Musim Panas (Summer)", "Musim Gugur (Autumn)", "Musim Dingin (Winter)"
    seasonDesc: String,              // Misal: "Bunga sakura sedang bermekaran di sepanjang taman kota."
    destinationDescription: String,  // Deskripsi tempat / daya tarik wisata
    imageUrl: String,                // URL Foto Lanskap Destinasi
    recommendedActivity: String,
    dailyBudget: String,
    isOutdoor: Boolean = true
) {
<<<<<<< HEAD
    div("space-y-6 mt-4 animate-slide-up") {

        // ==========================================
        // 1. HERO DESTINATION BANNER (Foto Tempat + Profil)
        // ==========================================
        div("relative rounded-3xl overflow-hidden border border-slate-800/80 shadow-2xl group") {
            // Gambar Background Lanskap Destinasi
            div("h-72 md:h-96 w-full bg-slate-900 relative overflow-hidden") {
                img(src = imageUrl, alt = countryName, classes = "w-full h-full object-cover group-hover:scale-105 transition-transform duration-700 brightness-90")
                
                // Gradient Overlay untuk Keterbacaan Teks
                div("absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/50 to-transparent")
            }

            // Konten Teks di Atas Banner Gambar
            div("absolute bottom-0 inset-x-0 p-6 md:p-8 flex flex-col md:flex-row items-start md:items-end justify-between gap-6 z-10") {
                div("max-w-2xl space-y-2") {
                    div("flex items-center gap-2.5 flex-wrap mb-2") {
                        span("px-3 py-1 rounded-full bg-sky-500/20 backdrop-blur-md text-sky-300 text-xs font-bold border border-sky-400/30") {
                            +"📍 $capital, $countryName"
=======
    // Evaluasi Tema Aksensuasi Berdasarkan Suhu
    val (weatherThemeBorder, weatherThemeGlow, weatherIcon, weatherBadgeBg) = when {
        temperature >= 26.0 -> Quadruple("hover:border-amber-500/50", "bg-amber-500/10 group-hover:bg-amber-500/20", "☀️", "bg-amber-500/10 text-amber-300 border-amber-500/30")
        temperature <= 15.0 -> Quadruple("hover:border-cyan-500/50", "bg-cyan-500/10 group-hover:bg-cyan-500/20", "❄️", "bg-cyan-500/10 text-cyan-300 border-cyan-500/30")
        else -> Quadruple("hover:border-emerald-500/50", "bg-emerald-500/10 group-hover:bg-emerald-500/20", "⛅", "bg-emerald-500/10 text-emerald-300 border-emerald-500/30")
    }

    div("grid grid-cols-1 md:grid-cols-2 gap-6 mt-2") {
        
        // 1. Profil Negara (Delay 100ms)
        div("group bg-slate-900/80 backdrop-blur-xl p-6 md:p-7 rounded-3xl border border-slate-800/80 shadow-xl hover:border-indigo-500/50 hover:-translate-y-1.5 transition-all duration-300 animate-slide-up delay-100 relative overflow-hidden") {
            div("absolute -right-10 -bottom-10 w-36 h-36 bg-indigo-500/10 rounded-full blur-2xl group-hover:bg-indigo-500/20 transition-all duration-500")
            
            div("relative z-10") {
                div("flex items-center justify-between mb-6") {
                    div("flex items-center gap-3") {
                        div("w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center text-2xl group-hover:scale-110 group-hover:rotate-6 transition-all duration-300") { 
                            +"🏛️" 
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30
                        }
                        span("px-3 py-1 rounded-full bg-emerald-500/20 backdrop-blur-md text-emerald-300 text-xs font-bold border border-emerald-400/30") {
                            +"💱 Mata Uang: $currencyCode"
                        }
                    }
                    h2("text-3xl md:text-5xl font-black text-white tracking-tight drop-shadow-md") {
                        +"$capital, $countryName"
                    }
                    p("text-slate-300 text-sm md:text-base leading-relaxed line-clamp-2 md:line-clamp-none backdrop-blur-sm bg-slate-950/30 p-3 rounded-2xl border border-white/10") {
                        +destinationDescription
                    }
                }
<<<<<<< HEAD

                // Widget Statistik Singkat
                div("flex gap-3 bg-slate-900/80 backdrop-blur-xl p-4 rounded-2xl border border-slate-800 shrink-0") {
                    div("text-center px-2") {
                        p("text-xs text-slate-400 font-medium") { +"Populasi" }
                        p("text-base font-bold text-white") { +"$population" }
                    }
                    div("w-px bg-slate-800")
                    div("text-center px-2") {
                        p("text-xs text-slate-400 font-medium") { +"Kode Valuta" }
                        p("text-base font-bold text-sky-400") { +currencyCode }
=======
                
                div("space-y-3 text-sm") {
                    fun item(label: String, value: String, icon: String) {
                        div("flex items-center justify-between p-3 rounded-2xl bg-slate-950/50 border border-slate-800/50 group-hover:border-slate-700/60 transition-colors") {
                            div("flex items-center gap-2.5 text-slate-400") {
                                span { +icon }
                                span { +label }
                            }
                            span("font-semibold text-slate-100") { +value }
                        }
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30
                    }
                }
            }
        }

<<<<<<< HEAD
        // ==========================================
        // 2. BENTO GRID: CUACA & MUSIM BERANIMASI
        // ==========================================
        div("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6") {

            // ------------------------------------------
            // KARTU A: CUACA REAL-TIME + ANIMASI DINAMIS
            // ------------------------------------------
            val isRainy = weatherDesc.contains("rain", ignoreCase = true) || weatherDesc.contains("hujan", ignoreCase = true)
            val isSnowy = weatherDesc.contains("snow", ignoreCase = true) || weatherDesc.contains("salju", ignoreCase = true)
            val isSunny = weatherDesc.contains("sun", ignoreCase = true) || weatherDesc.contains("cerah", ignoreCase = true)

            val weatherGlow = when {
                isRainy -> "hover:border-sky-500/60 bg-sky-950/30"
                isSnowy -> "hover:border-indigo-400/60 bg-indigo-950/30"
                isSunny -> "hover:border-amber-500/60 bg-amber-950/30"
                else -> "hover:border-cyan-500/60 bg-slate-900/80"
            }

            div("group bg-slate-900/80 backdrop-blur-xl p-6 rounded-3xl border border-slate-800/80 shadow-xl $weatherGlow hover:-translate-y-1.5 transition-all duration-300 relative overflow-hidden flex flex-col justify-between") {
                
                // ELEMEN ANIMASI CUACA SESUAI KONDISI
                div("absolute top-3 right-4 pointer-events-none") {
                    if (isRainy) {
                        // Tetesan Hujan Beranimasi
                        div("flex gap-2 h-16 items-start opacity-70") {
                            repeat(5) { i ->
                                div("w-1 bg-sky-400 rounded-full animate-rain") {
                                    style = "height: ${12 + (i * 4)}px; animation-delay: ${i * 150}ms;"
                                }
                            }
                        }
                    } else if (isSnowy) {
                        // Salju Melayang Beranimasi
                        div("flex gap-3 text-lg text-indigo-200 opacity-80") {
                            repeat(3) { i ->
                                span("animate-snow inline-block") {
                                    style = "animation-delay: ${i * 400}ms;"
                                    +"❄️"
                                }
                            }
                        }
                    } else if (isSunny) {
                        // Sinar Matahari Berpijar
                        div("text-5xl animate-sun opacity-90") { +"☀️" }
                    } else {
                        // Awan Bergerak
                        div("text-4xl animate-cloud opacity-80") { +"☁️" }
                    }
                }

                div("relative z-10") {
                    div("flex items-center gap-3 mb-4") {
                        div("w-12 h-12 rounded-2xl bg-slate-800/80 border border-slate-700/80 text-amber-400 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform") { 
                            if (isRainy) +"🌧️" else if (isSnowy) +"❄️" else if (isSunny) +"☀️" else +"⛅" 
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Cuaca Real-Time" }
                            p("text-slate-400 text-xs") { +"Kondisi Langsung di Tujuan" }
                        }
                    }

                    div("flex flex-col items-center justify-center py-5 my-2 bg-slate-950/50 rounded-2xl border border-slate-800/50 relative overflow-hidden") {
                        div("text-6xl font-black text-transparent bg-clip-text bg-gradient-to-b from-white via-slate-100 to-sky-200 tracking-tighter") { 
                            +"$temperature°C" 
                        }
                        span("mt-2 px-3.5 py-1 rounded-full bg-sky-500/10 border border-sky-500/30 text-sky-300 font-semibold text-xs capitalize") {
                            +weatherDesc
                        }
                    }
=======
        // 2. Analisis Cuaca Real-Time (Delay 200ms - Adaptive Styling)
        div("group bg-slate-900/80 backdrop-blur-xl p-6 md:p-7 rounded-3xl border border-slate-800/80 shadow-xl $weatherThemeBorder hover:-translate-y-1.5 transition-all duration-300 animate-slide-up delay-200 relative overflow-hidden") {
            div("absolute -right-10 -bottom-10 w-36 h-36 $weatherThemeGlow rounded-full blur-2xl transition-all duration-500")

            div("relative z-10") {
                div("flex items-center justify-between mb-4") {
                    div("flex items-center gap-3") {
                        div("w-12 h-12 rounded-2xl bg-slate-800/80 border border-slate-700/80 text-amber-400 flex items-center justify-center text-2xl group-hover:scale-110 group-hover:-rotate-6 transition-all duration-300") { 
                            +weatherIcon 
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Cuaca Real-Time" }
                            p("text-slate-400 text-xs") { +"Kondisi Terkini" }
                        }
                    }
                    span("inline-flex items-center gap-1.5 px-3 py-1 rounded-full $weatherBadgeBg text-xs font-semibold border capitalize") {
                        span("w-1.5 h-1.5 rounded-full bg-current animate-pulse")
                        +weatherDesc
                    }
                }

                div("flex flex-col items-center justify-center py-4 my-2 bg-slate-950/40 rounded-2xl border border-slate-800/40") {
                    div("text-6xl font-extrabold text-transparent bg-clip-text bg-gradient-to-b from-white via-slate-100 to-slate-300 tracking-tighter group-hover:scale-105 transition-transform duration-300") { 
                        +"$temperature°C" 
                    }
                    p("text-slate-400 text-xs mt-1 capitalize") { +"Deskripsi: $weatherDesc" }
                }
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30

                    div("mt-4 space-y-1.5") {
                        div("flex justify-between text-xs font-semibold text-slate-400") {
                            span { +"Tingkat Kelembapan Udara" }
                            span("text-sky-400 font-bold") { +"$humidity%" }
                        }
                        div("w-full bg-slate-950 rounded-full h-2.5 p-0.5 border border-slate-800 overflow-hidden") {
                            div("bg-gradient-to-r from-sky-500 to-indigo-400 h-full rounded-full transition-all duration-1000") {
                                style = "width: $humidity%"
                            }
                        }
                    }
                }
            }

<<<<<<< HEAD
            // ------------------------------------------
            // KARTU B: MUSIM & ANIMASI SUASANA
            // ------------------------------------------
            val isSpring = seasonName.contains("semi", ignoreCase = true) || seasonName.contains("spring", ignoreCase = true)
            val isAutumn = seasonName.contains("gugur", ignoreCase = true) || seasonName.contains("autumn", ignoreCase = true)
            val isWinter = seasonName.contains("dingin", ignoreCase = true) || seasonName.contains("winter", ignoreCase = true)

            val (seasonIcon, seasonColor, seasonBadge) = when {
                isSpring -> Triple("🌸", "border-pink-500/50 bg-pink-950/20", "bg-pink-500/10 text-pink-300 border-pink-500/30")
                isAutumn -> Triple("🍂", "border-amber-500/50 bg-amber-950/20", "bg-amber-500/10 text-amber-300 border-amber-500/30")
                isWinter -> Triple("❄️", "border-blue-500/50 bg-blue-950/20", "bg-blue-500/10 text-blue-300 border-blue-500/30")
                else -> Triple("☀️", "border-emerald-500/50 bg-emerald-950/20", "bg-emerald-500/10 text-emerald-300 border-emerald-500/30")
            }

            div("group bg-slate-900/80 backdrop-blur-xl p-6 rounded-3xl border border-slate-800/80 shadow-xl $seasonColor hover:-translate-y-1.5 transition-all duration-300 relative overflow-hidden flex flex-col justify-between") {
                
                // ELEMEN ANIMASI PARTIKEL MUSIM
                div("absolute top-2 right-4 pointer-events-none") {
                    if (isSpring) {
                        div("flex gap-2 text-base animate-leaf opacity-80") { repeat(3) { +"🌸" } }
                    } else if (isAutumn) {
                        div("flex gap-2 text-base animate-leaf opacity-80") { repeat(3) { +"🍂" } }
                    } else if (isWinter) {
                        div("flex gap-2 text-base animate-snow opacity-80") { repeat(3) { +"❄️" } }
                    } else {
                        div("text-3xl animate-sun opacity-80") { +"☀️" }
                    }
                }

                div("relative z-10") {
                    div("flex items-center gap-3 mb-4") {
                        div("w-12 h-12 rounded-2xl bg-slate-800/80 border border-slate-700/80 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform") { 
                            +seasonIcon 
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Musim Terkini" }
                            p("text-slate-400 text-xs") { +"Kondisi Musim Lokal" }
                        }
                    }

                    div("p-4 rounded-2xl bg-slate-950/50 border border-slate-800/50 mb-3") {
                        span("inline-block px-3 py-1 rounded-full $seasonBadge text-xs font-bold mb-2 border") {
                            +seasonName
                        }
                        p("text-slate-300 text-xs md:text-sm leading-relaxed") {
                            +seasonDesc
                        }
                    }
                }

                div("text-xs text-slate-500 flex items-center gap-1.5 font-medium") {
                    span("w-2 h-2 rounded-full bg-emerald-400 animate-pulse")
                    span { +"Rekomendasi pakaian disesuaikan dengan musim" }
=======
        // 3. Rekomendasi Aktivitas Otomatis (Delay 300ms - Dynamic Tagging)
        div("group bg-slate-900/80 backdrop-blur-xl p-6 md:p-7 rounded-3xl border border-slate-800/80 shadow-xl hover:border-emerald-500/50 hover:-translate-y-1.5 transition-all duration-300 animate-slide-up delay-300 relative overflow-hidden") {
            div("absolute -right-10 -bottom-10 w-36 h-36 bg-emerald-500/10 rounded-full blur-2xl group-hover:bg-emerald-500/20 transition-all duration-500")

            div("relative z-10") {
                div("flex items-center justify-between mb-4") {
                    div("flex items-center gap-3") {
                        div("w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center text-2xl group-hover:scale-110 transition-all duration-300") { 
                            +"🎯" 
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Rekomendasi Aktivitas" }
                            p("text-slate-400 text-xs") { +"Penyesuaian Cuaca Dinamis" }
                        }
                    }
                }

                // Dynamic Tagging Badges
                div("flex gap-2 mb-3") {
                    if (isOutdoor) {
                        span("px-2.5 py-1 rounded-lg bg-emerald-500/10 text-emerald-400 text-xs font-semibold border border-emerald-500/20") {
                            +"🌲 Outdoor Recommended"
                        }
                    } else {
                        span("px-2.5 py-1 rounded-lg bg-purple-500/10 text-purple-400 text-xs font-semibold border border-purple-500/20") {
                            +"🏛️ Indoor Recommended"
                        }
                    }
                    span("px-2.5 py-1 rounded-lg bg-cyan-500/10 text-cyan-400 text-xs font-semibold border border-cyan-500/20") {
                        +"Smart Match"
                    }
                }

                div("p-4 rounded-2xl bg-gradient-to-br from-emerald-950/40 via-slate-950/60 to-slate-950/80 border border-emerald-500/20 group-hover:border-emerald-500/40 transition-colors") {
                    p("text-emerald-200 font-medium leading-relaxed text-sm md:text-base") {
                        +recommendedActivity
                    }
                }
                
                div("mt-4 flex items-center gap-2 text-xs text-slate-500") {
                    span("w-2 h-2 rounded-full bg-emerald-400 animate-ping")
                    span { +"Generasi otomatis via Polymorphic Recommender Engine" }
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30
                }
            }

<<<<<<< HEAD
            // ------------------------------------------
            // KARTU C: REKOMENDASI AKTIVITAS & BUDGET
            // ------------------------------------------
            div("group bg-slate-900/80 backdrop-blur-xl p-6 rounded-3xl border border-slate-800/80 shadow-xl hover:border-emerald-500/50 hover:-translate-y-1.5 transition-all duration-300 relative overflow-hidden flex flex-col justify-between md:col-span-2 lg:col-span-1") {
                div("relative z-10 space-y-4") {
                    div("flex items-center gap-3") {
                        div("w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform") { 
                            +"🎯" 
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Rekomendasi & Budget" }
                            p("text-slate-400 text-xs") { +"Saran Aktivitas Otomatis" }
                        }
=======
        // 4. Estimasi Anggaran Harian (Delay 400ms)
        div("group bg-slate-900/80 backdrop-blur-xl p-6 md:p-7 rounded-3xl border border-slate-800/80 shadow-xl hover:border-purple-500/50 hover:-translate-y-1.5 transition-all duration-300 animate-slide-up delay-400 relative overflow-hidden flex flex-col justify-between") {
            div("absolute -right-10 -bottom-10 w-36 h-36 bg-purple-500/10 rounded-full blur-2xl group-hover:bg-purple-500/20 transition-all duration-500")

            div("relative z-10") {
                div("flex items-center gap-3 mb-4") {
                    div("w-12 h-12 rounded-2xl bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center text-2xl group-hover:scale-110 transition-all duration-300") { 
                        +"💳" 
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30
                    }

                    div("p-4 rounded-2xl bg-emerald-950/30 border border-emerald-500/20 space-y-2") {
                        div("flex items-center gap-2") {
                            span("px-2.5 py-0.5 rounded-md bg-emerald-500/20 text-emerald-300 text-[11px] font-bold") {
                                if (isOutdoor) +"🌲 Outdoor Recommended" else +"🏛️ Indoor Recommended"
                            }
                        }
                        p("text-emerald-100 text-xs md:text-sm leading-relaxed font-medium") {
                            +recommendedActivity
                        }
                    }

                    div("p-4 rounded-2xl bg-purple-950/30 border border-purple-500/20 flex justify-between items-center") {
                        div {
                            p("text-[11px] text-slate-400 font-medium") { +"Estimasi Budget Harian" }
                            p("text-xl md:text-2xl font-black text-purple-300") { +dailyBudget }
                        }
                        span("text-2xl") { +"💳" }
                    }
                }
            }
        }
    }
}

<<<<<<< HEAD
// Widget Error / Empty State Card
fun FlowContent.weatherErrorWidget(errorMessage: String) {
    div("p-8 rounded-3xl bg-slate-900/90 border border-rose-500/30 text-center max-w-lg mx-auto my-6 animate-slide-up backdrop-blur-xl shadow-2xl") {
        div("w-16 h-16 mx-auto mb-4 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400 flex items-center justify-center text-3xl") { 
            +"📍" 
        }
        h4("text-xl font-bold text-white mb-2") { +"Destinasi Tidak Ditemukan" }
        p("text-slate-400 text-sm mb-4 leading-relaxed") { +errorMessage }
        div("px-4 py-2 rounded-xl bg-slate-950 text-slate-500 text-xs font-mono inline-block border border-slate-800") {
            +"Coba cari dengan ejaan lain, misal: Tokyo, Paris, Bali, London."
=======
// Helper Data Holder untuk Tema Cuaca
private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// Card Penanganan Error / Empty State
fun FlowContent.weatherErrorWidget(errorMessage: String) {
    div("p-8 rounded-3xl bg-slate-900/90 border border-rose-500/30 text-center max-w-lg mx-auto my-6 animate-slide-up backdrop-blur-xl shadow-2xl") {
        div("w-16 h-16 mx-auto mb-4 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400 flex items-center justify-center text-3xl") { 
            +"⚠️" 
        }
        h4("text-xl font-bold text-white mb-2") { +"Pencarian Tidak Ditemukan" }
        p("text-slate-400 text-sm mb-4 leading-relaxed") { +errorMessage }
        div("px-4 py-2 rounded-xl bg-slate-950 text-slate-500 text-xs font-mono inline-block border border-slate-800") {
            +"Pastikan nama kota/negara ditulis dengan benar."
>>>>>>> ae74dc79e545301452fc65ebabb65fdb7dee8c30
        }
    }
}