package com.travelwise.view

import kotlinx.html.*

fun HTML.mainLayout(content: DIV.() -> Unit) {
    head {
        title { +"TRAVELWISE // TACTICAL TELEMETRY ENGINE" }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
        
        script { src = "https://cdn.tailwindcss.com" }
        script { src = "https://unpkg.com/htmx.org@1.9.10" }
        
        // Font Brutalist: Space Grotesk (Macro Header) & JetBrains Mono (Data Telemetry)
        link { href = "https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600;800&family=Space+Grotesk:wght@700;900&display=swap"; rel = "stylesheet" }
        
        style {
            unsafe {
                raw("""
                    body { font-family: 'JetBrains Mono', monospace; }
                    h1, h2, h3, .font-heading { font-family: 'Space Grotesk', sans-serif; }

                    /* CRT Scanline Effect Overlay */
                    .crt-overlay {
                        background: repeating-linear-gradient(
                            0deg,
                            rgba(0, 0, 0, 0.15),
                            rgba(0, 0, 0, 0.15) 1px,
                            transparent 1px,
                            transparent 2px
                        );
                        pointer-events: none;
                    }

                    /* HTMX Indicator State */
                    .htmx-indicator { display: none; opacity: 0; }
                    .htmx-request .htmx-indicator, .htmx-request.htmx-indicator { display: block; opacity: 1; }
                    
                    /* Brutalist Animations */
                    @keyframes rainDropBrutalist {
                        0% { transform: translateY(-10px); opacity: 0; }
                        50% { opacity: 1; }
                        100% { transform: translateY(60px); opacity: 0; }
                    }
                    .animate-rain-brutal { animation: rainDropBrutalist 0.5s linear infinite; }

                    @keyframes pulseBlink {
                        0%, 100% { opacity: 1; }
                        50% { opacity: 0.2; }
                    }
                    .animate-blink { animation: pulseBlink 1s infinite steps(1); }
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
    body("bg-[#0d0f12] text-slate-100 min-h-screen relative selection:bg-amber-400 selection:text-black border-t-4 border-amber-500") {
        
        // Scanlines Overlay Effect
        div("fixed inset-0 crt-overlay z-40 pointer-events-none")

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
                        span("w-2 h-2 bg-emerald-400 animate-blink")
                        +"SYSTEM_ONLINE"
                    }
                }
            }
        }
        
        div("container mx-auto px-4 py-8 md:py-12 relative z-10") {
            content()
        }

        footer("mt-20 py-8 border-t-2 border-slate-800 bg-[#090a0c] text-slate-500 text-xs font-mono text-center") {
            div("container mx-auto px-4 uppercase tracking-widest") {
                +"[ SYSTEM: TRAVELWISE OLEH KIYOSHI ] // INDUSTRIAL TELEMETRY INTERFACE // KTOR + HTMX ENGINE"
            }
        }
    }
}