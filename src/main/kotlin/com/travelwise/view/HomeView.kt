package com.travelwise.view

import com.travelwise.model.BudgetTier
import kotlinx.html.*

/** Halaman utama: hero + form pencarian + pilihan tier budget (HTMX). */
fun HTML.homeView() = layoutPage("TravelWise - Smart Travel Assistant") {
    section("text-center mb-10") {
        h1("text-4xl sm:text-5xl font-extrabold bg-gradient-to-r from-sky-400 to-indigo-400 bg-clip-text text-transparent") {
            +"TravelWise"
        }
        p("mt-3 text-slate-400") {
            +"Profil negara, cuaca real-time, tempat wisata asli, dan estimasi budget harian."
        }
    }

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
}