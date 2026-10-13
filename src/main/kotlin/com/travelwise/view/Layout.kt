package com.travelwise.view

import kotlinx.html.*

fun HTML.mainLayout(content: DIV.() -> Unit) {
    head {
        title { +"Travel Wise | Smart Travel Companion" }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
        
        // Memuat Tailwind CSS melalui CDN
        script { src = "https://cdn.tailwindcss.com" }
        
        // Memuat HTMX untuk AJAX dinamis
        script { src = "https://unpkg.com/htmx.org@1.9.10" }
        
        // Tambahan styling khusus untuk efek loading HTMX
        style {
            unsafe {
                raw("""
                    .htmx-indicator { display: none; }
                    .htmx-request .htmx-indicator { display: inline-block; }
                    .htmx-request.htmx-indicator { display: inline-block; }
                """)
            }
        }
    }
    body("bg-slate-50 font-sans leading-normal tracking-normal text-slate-800") {
        // Navigasi Sederhana
        nav("bg-blue-600 p-4 shadow-md") {
            div("container mx-auto flex items-center justify-between") {
                div("text-white font-bold text-2xl tracking-wide") { +"✈️ TravelWise" }
            }
        }
        
        // Konten Dinamis akan di-inject di sini
        div("container mx-auto p-4 md:p-8") {
            content()
        }
    }
}