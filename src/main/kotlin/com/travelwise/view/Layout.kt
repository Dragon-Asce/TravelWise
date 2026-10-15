// Layout.kt
package com.travelwise.view

import kotlinx.html.*

fun HTML.mainLayout(content: DIV.() -> Unit) {
    head {
        title { +"TRAVELWISE // SWISS TOURISM INDEX" }
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        
        // Tailwind & HTMX
        script { src = "https://cdn.tailwindcss.com" }
        script { src = "https://unpkg.com/htmx.org@1.9.10" }
        
        // Font Swiss Brutalism: Inter
        link { href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;900&display=swap"; rel = "stylesheet" }
        
        // Leaflet CSS & JS untuk Peta
        link { href = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"; rel = "stylesheet" }
        script { src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" }
        
        style {
            unsafe {
                raw("""
                    /* SWISS TYPOGRAPHY & BRUTALIST RULES */
                    * {
                        border-radius: 0px !important;
                        font-family: 'Inter', 'Helvetica Neue', Helvetica, Arial, sans-serif;
                        letter-spacing: -0.02em;
                    }
                    
                    body {
                        background-color: #F4F4F0;
                        color: #050505;
                        -webkit-font-smoothing: antialiased;
                    }

                    /* Macro-Typography */
                    .text-macro {
                        font-size: clamp(3rem, 8vw, 8rem);
                        line-height: 0.85;
                        letter-spacing: -0.05em;
                        font-weight: 900;
                        text-transform: uppercase;
                    }

                    /* Structural Borders */
                    .swiss-border { border: 2px solid #050505; }
                    .swiss-border-b { border-bottom: 2px solid #050505; }
                    .swiss-border-t { border-top: 2px solid #050505; }
                    .swiss-border-l { border-left: 2px solid #050505; }
                    .swiss-border-r { border-right: 2px solid #050505; }

                    /* Custom Radio Buttons for Budget Tier */
                    input[type="radio"]:checked + label {
                        background-color: #050505;
                        color: #ffffff;
                    }

                    /* Photography - Full Color tapi Kontras Tinggi */
                    .img-swiss {
                        filter: contrast(110%) saturate(120%);
                        transition: transform 0.4s ease;
                    }
                    
                    .img-wrap:hover .img-swiss {
                        transform: scale(1.02);
                    }

                    /* Animations */
                    @keyframes blink { 0%, 100% { opacity: 1; } 50% { opacity: 0; } }
                    .animate-blink { animation: blink 1s step-end infinite; }
                    
                    @keyframes slideUp {
                        from { opacity: 0; transform: translateY(15px); }
                        to { opacity: 1; transform: translateY(0); }
                    }
                    .animate-enter { animation: slideUp 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
                    
                    @keyframes ticker {
                        0% { transform: translateX(100%); }
                        100% { transform: translateX(-100%); }
                    }
                    .animate-ticker {
                        display: inline-block;
                        white-space: nowrap;
                        animation: ticker 20s linear infinite;
                    }

                    /* HTMX Indicator State */
                    .htmx-indicator { display: none; opacity: 0; }
                    .htmx-request .htmx-indicator, .htmx-request.htmx-indicator { display: block; opacity: 1; }
                    
                    /* Map Filters for Brutalist Look (Grayscale/Invert) */
                    .leaflet-layer,
                    .leaflet-control-zoom-in,
                    .leaflet-control-zoom-out,
                    .leaflet-control-attribution {
                        filter: grayscale(100%) contrast(150%) invert(100%);
                    }
                """)
            }
        }
        
        // Global Keyboard Shortcut (Ctrl+K)
        script {
            unsafe {
                raw("""
                    document.addEventListener('keydown', function(e) {
                        if ((e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k') {
                            e.preventDefault();
                            const input = document.getElementById('search-input');
                            if (input) {
                                input.focus();
                                input.select();
                            }
                        }
                    });
                """)
            }
        }
    }
    body("min-h-screen flex flex-col p-4 md:p-8 selection:bg-[#E61919] selection:text-white relative") {
        
        // 1. Noise SVG Overlay
        div("pointer-events-none fixed inset-0 z-50 opacity-[0.03]") {
            unsafe {
                raw("""
                    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" class="w-full h-full">
                        <filter id="noiseFilter"><feTurbulence type="fractalNoise" baseFrequency="0.85" numOctaves="3" stitchTiles="stitch"/></filter>
                        <rect width="100%" height="100%" filter="url(#noiseFilter)"/>
                    </svg>
                """)
            }
        }
        
        // 2. Ticker Tape (Data Log Bergerak)
        div("max-w-7xl mx-auto w-full border-b-2 border-[#050505] bg-[#050505] text-[#E61919] overflow-hidden py-1 mb-4") {
            div("text-[10px] font-mono font-bold uppercase tracking-widest animate-ticker") {
                +"/// SYSTEM LIVE FEED /// EXCHANGE RATE: 1 USD = 15,500 IDR (UPDATED 2 MINS AGO) /// GLOBAL METEOROLOGY: ACTIVE /// OPENSTREETMAP POI: CONNECTED /// KUNSTHAUS SWISS DESIGN ENABLED ///"
            }
        }

        // HEADER / NAVIGATION
        header("max-w-7xl mx-auto w-full swiss-border-b pb-4 mb-8 flex flex-col md:flex-row md:items-end justify-between gap-4") {
            div("flex items-end gap-3") {
                div("w-14 h-14 bg-[#E61919] text-white flex items-center justify-center font-black text-3xl") { +"TW" }
                div {
                    h1("text-3xl font-black uppercase tracking-tighter") { +"TRAVELWISE" }
                    p("text-xs font-bold uppercase tracking-widest text-gray-500 mt-1") { +"OPENSTREETMAP & WEATHER INTEGRATION / V.26" }
                }
            }
            
            div("text-xs font-bold uppercase text-right") {
                span("text-[#E61919] animate-blink block") { +"[ SYSTEM STANDBY ]" }
                span("text-gray-500") { +"SHORTCUT: CTRL+K TO SEARCH" }
            }
        }

        // MAIN CONTAINER
        main("max-w-7xl mx-auto w-full flex-grow relative overflow-hidden z-10") {
            content()
        }
        
        // FOOTER
        footer("max-w-7xl mx-auto w-full mt-24 pt-4 swiss-border-t flex flex-col md:flex-row justify-between text-xs font-bold uppercase text-gray-500 z-10") {
            p { +"© 2026 / TRAVELWISE CORP." }
            p { +"SWISS TYPOGRAPHIC ARCHITECTURE" }
        }
    }
}