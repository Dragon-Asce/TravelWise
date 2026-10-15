package com.travelwise.view

import kotlinx.html.*

// PERBAIKAN 1: Mengubah nama parameter dari 'content' menjadi 'pageContent'
fun HTML.mainLayout(pageContent: DIV.() -> Unit) {
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

                    /* Photography */
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
                    
                    /* Clean Brutalist Map Styling (Fix Blackscreen) */
                    .leaflet-tile {
                        filter: grayscale(100%) contrast(120%);
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
    body(classes = "min-h-screen flex flex-col p-4 md:p-8 selection:bg-[#E61919] selection:text-white relative") {

        // PERBAIKAN 2: Menambahkan {} pada div overlay yang kosong
        div("fixed inset-0 crt-overlay z-40 pointer-events-none") {}

        // Navbar Brutalis
        nav("border-b-2 border-slate-800 bg-[#12151a] sticky top-0 z-50") {
            div("container mx-auto px-6 py-4 flex items-center justify-between") {
                div("flex items-center gap-3 cursor-pointer") {
                    div("w-9 h-9 bg-amber-400 text-black border-2 border-black flex items-center justify-center font-black text-xl shadow-[3px_3px_0px_0px_#ffffff]") {
                        +"TW"
                    }
                    div {
                        span("text-xl font-black font-heading tracking-widest text-white uppercase block leading-none") {
                            +"TRAVELWISE //"
                        }
                        span("text-[10px] text-amber-400 font-mono tracking-tighter uppercase") {
                            +"[ TELEMETRY_SYS_V2.6 ]"
                        }
                    }
                }

                div("flex items-center gap-4") {
                    div("hidden md:flex items-center gap-2 px-3 py-1 bg-slate-900 border border-slate-700 text-xs font-mono text-slate-300") {
                        span("text-amber-400 font-bold") { +"CTRL+K" }
                        span { +"//" }
                        span { +"QUICK_INPUT" }
                    }
                    div("flex items-center gap-2 px-3 py-1 bg-emerald-500/10 border border-emerald-500 text-emerald-400 text-xs font-bold uppercase") {
                        // PERBAIKAN 2: Menambahkan {} pada span indikator yang kosong
                        span("w-2 h-2 bg-emerald-400 animate-blink") {}
                        +"SYSTEM_ONLINE"
                    }
                }
            }
        }

        div("container mx-auto px-4 py-8 md:py-12 relative z-10") {
            // PERBAIKAN 1: Memanggil pageContent()
            pageContent()
        }

        footer("mt-20 py-8 border-t-2 border-slate-800 bg-[#090a0c] text-slate-500 text-xs font-mono text-center") {
            div("container mx-auto px-4 uppercase tracking-widest") {
                +"[ SYSTEM: TRAVELWISE OLEH KIYOSHI ] // INDUSTRIAL TELEMETRY INTERFACE // KTOR + HTMX ENGINE"
            }
        }
    }
}