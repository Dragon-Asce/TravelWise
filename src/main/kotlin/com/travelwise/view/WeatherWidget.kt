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
    recommendedActivity: String,
    dailyBudget: String,
    isOutdoor: Boolean = true
) {
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
                        }
                        div {
                            h3("text-lg font-bold text-white") { +"Profil Destinasi" }
                            p("text-slate-400 text-xs") { +"Informasi Resmi Negara" }
                        }
                    }
                    span("px-3 py-1 rounded-full bg-slate-800 text-slate-300 text-xs font-semibold border border-slate-700") {
                        +currencyCode
                    }
                }
                
                div("space-y-3 text-sm") {
                    fun item(label: String, value: String, icon: String) {
                        div("flex items-center justify-between p-3 rounded-2xl bg-slate-950/50 border border-slate-800/50 group-hover:border-slate-700/60 transition-colors") {
                            div("flex items-center gap-2.5 text-slate-400") {
                                span { +icon }
                                span { +label }
                            }
                            span("font-semibold text-slate-100") { +value }
                        }
                    }
                    item("Negara Tujuan", countryName, "🗺️")
                    item("Ibu Kota", capital, "📍")
                    item("Total Populasi", "$population Jiwa", "👥")
                    item("Kode Valuta", currencyCode, "💱")
                }
            }
        }

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

                div("mt-4") {
                    div("flex justify-between text-xs font-semibold text-slate-400 mb-2") {
                        span { +"Tingkat Kelembapan" }
                        span("text-amber-400 font-bold") { +"$humidity%" }
                    }
                    div("w-full bg-slate-950 rounded-full h-3 p-0.5 border border-slate-800 overflow-hidden") {
                        div("bg-gradient-to-r from-amber-500 via-orange-400 to-amber-300 h-full rounded-full transition-all duration-1000 ease-out shadow-sm shadow-amber-500/50") {
                            style = "width: $humidity%"
                        }
                    }
                }
            }
        }

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
                }
            }
        }

        // 4. Estimasi Anggaran Harian (Delay 400ms)
        div("group bg-slate-900/80 backdrop-blur-xl p-6 md:p-7 rounded-3xl border border-slate-800/80 shadow-xl hover:border-purple-500/50 hover:-translate-y-1.5 transition-all duration-300 animate-slide-up delay-400 relative overflow-hidden flex flex-col justify-between") {
            div("absolute -right-10 -bottom-10 w-36 h-36 bg-purple-500/10 rounded-full blur-2xl group-hover:bg-purple-500/20 transition-all duration-500")

            div("relative z-10") {
                div("flex items-center gap-3 mb-4") {
                    div("w-12 h-12 rounded-2xl bg-purple-500/10 border border-purple-500/20 text-purple-400 flex items-center justify-center text-2xl group-hover:scale-110 transition-all duration-300") { 
                        +"💳" 
                    }
                    div {
                        h3("text-lg font-bold text-white") { +"Estimasi Anggaran Harian" }
                        p("text-slate-400 text-xs") { +"Kalkulasi Nilai Tukar" }
                    }
                }

                p("text-slate-400 text-xs leading-relaxed mb-4") {
                    +"Proyeksi rata-rata pengeluaran harian (makan, akomodasi ringan, transportasi) dalam mata uang lokal:"
                }

                div("p-5 rounded-2xl bg-gradient-to-r from-purple-950/40 via-indigo-950/30 to-slate-950/60 border border-purple-500/20 flex flex-col items-center justify-center text-center group-hover:border-purple-500/40 transition-colors") {
                    div("text-3xl md:text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-300 via-indigo-200 to-cyan-300 tracking-tight") {
                        +dailyBudget
                    }
                    span("text-[10px] text-slate-500 mt-2 font-mono uppercase tracking-widest") {
                        +"*Berbasis konversi realtime ($currencyCode)"
                    }
                }
            }
        }
    }
}

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
        }
    }
}