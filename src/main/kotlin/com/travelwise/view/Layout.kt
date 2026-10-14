package com.travelwise.view

import kotlinx.html.*

/** Template dasar HTML + Tailwind (CDN) + HTMX. */
fun HTML.layoutPage(pageTitle: String, content: MAIN.() -> Unit) {
    head {
        meta(charset = "utf-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1")
        title(pageTitle)
        script(src = "https://cdn.tailwindcss.com") {}
        script(src = "https://unpkg.com/htmx.org@1.9.12") {}
        style {
            unsafe {
                +"""
                .htmx-indicator { display: none; }
                .htmx-request .htmx-indicator { display: flex; }
                @keyframes slideUp { from { opacity: 0; transform: translateY(16px); } to { opacity: 1; transform: none; } }
                .animate-slide-up { animation: slideUp .5s ease-out both; }
                """.trimIndent()
            }
        }
    }
    body("min-h-screen bg-slate-950 text-slate-100 antialiased") {
        main("max-w-5xl mx-auto px-4 py-10") { content() }
    }
}