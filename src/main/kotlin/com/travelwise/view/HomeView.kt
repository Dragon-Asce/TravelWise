package com.travelwise.view

import kotlinx.html.*

fun HTML.homeView() {
    mainLayout {
        div("max-w-4xl mx-auto") {
            // Hero Section
            div("text-center mb-12 animate-slide-up") {
                div("inline-flex items-center gap-2 py-2 px-4 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 font-medium text-xs md:text-sm mb-6 backdrop-blur-md shadow-inner") {
                    span("text-base") { +"✨" }
                    +"Next-Gen Smart Travel Assistant"
                }
                h1("text-4xl md:text-6xl font-black text-white mb-6 leading-tight tracking-tight") {
                    +"Eksplorasi Destinasi "
                    br()
                    span("bg-clip-text text-transparent bg-gradient-to-r from-indigo-400 via-cyan-300 to-emerald-400 animate-pulse-glow") {
                        +"Tanpa Keraguan."
                    }
                }
                p("text-slate-400 text-base md:text-lg max-w-2xl mx-auto leading-relaxed") {
                    +"Cukup ketik nama kota atau negara. Sistem akan menganalisis cuaca real-time, menyusun rekomendasi aktivitas, dan menghitung estimasi budget secara otomatis."
                }
            }

            // Glassmorphism Search Box dengan Hover Glow & Micro-interactions
            div("group relative max-w-3xl mx-auto mb-12 animate-slide-up delay-100") {
                div("absolute -inset-1 bg-gradient-to-r from-indigo-500 via-cyan-500 to-emerald-500 rounded-[2.5rem] blur-xl opacity-25 group-hover:opacity-60 transition duration-700 group-hover:duration-200")

                div("relative bg-slate-900/90 backdrop-blur-2xl p-3 md:p-4 rounded-[2.2rem] border border-slate-800 shadow-2xl") {
                    form {
                        attributes["hx-get"] = "/api/search"
                        attributes["hx-target"] = "#widget-container"
                        attributes["hx-indicator"] = "#loading-spinner"
                        attributes["hx-swap"] = "innerHTML"

                        div("flex flex-col md:flex-row gap-3 relative") {
                            div("relative flex-1 flex items-center") {
                                div("absolute left-5 text-xl text-slate-400 transition-transform duration-300 group-focus-within:scale-125 group-focus-within:text-indigo-400") {
                                    +"📍"
                                }
                                input(InputType.text, name = "destination") {
                                    classes = setOf(
                                        "w-full", "pl-14", "pr-6", "py-4", "md:py-5", "bg-slate-950/60", "border", "border-slate-800",
                                        "rounded-2xl", "text-base", "md:text-lg", "font-medium", "text-white", "placeholder-slate-500",
                                        "focus:outline-none", "focus:border-indigo-500/80", "focus:ring-4", "focus:ring-indigo-500/20",
                                        "transition-all", "duration-300"
                                    )
                                    placeholder = "Mau ke mana? (Contoh: Tokyo, Zurich, Bali...)"
                                    required = true
                                }
                            }
                            button(type = ButtonType.submit) {
                                classes = setOf(
                                    "bg-gradient-to-r", "from-indigo-600", "via-indigo-500", "to-cyan-500",
                                    "hover:from-indigo-500", "hover:to-cyan-400",
                                    "text-white", "font-bold", "text-base", "md:text-lg", "py-4", "px-8", "rounded-2xl",
                                    "shadow-lg", "shadow-indigo-500/25", "hover:shadow-indigo-500/50",
                                    "active:scale-95", "transition-all", "duration-300",
                                    "flex", "items-center", "justify-center", "gap-2", "cursor-pointer"
                                )
                                span { +"Cari Destinasi" }
                                span("text-xl group-hover:translate-x-1 transition-transform duration-300") { +"🚀" }
                            }
                        }
                    }
                }
            }

            // Radar Pulse Loading Animation
            div("htmx-indicator flex-col items-center justify-center my-16 py-8 animate-slide-up") {
                id = "loading-spinner"
                div("relative flex items-center justify-center w-20 h-20") {
                    div("absolute w-full h-full rounded-full bg-indigo-500/20 animate-ping")
                    div("absolute w-12 h-12 rounded-full bg-indigo-500/40 animate-pulse")
                    div("relative w-8 h-8 rounded-full bg-indigo-500 flex items-center justify-center text-white text-xs font-bold shadow-lg shadow-indigo-500/50") {
                        +"⚡"
                    }
                }
                p("text-slate-300 mt-5 font-semibold text-sm tracking-wide animate-pulse") {
                    +"Menganalisis cuaca real-time & menghitung estimasi..."
                }
            }

            div("min-h-[200px]") {
                id = "widget-container"
            }
        }
    }
}