package com.travelwise.view

import kotlinx.html.*

fun HTML.mainLayout(content: DIV.() -> Unit) {
    head {
        title { +"TravelWise | Smart Travel Companion" }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1.0" }
        
        script { src = "https://cdn.tailwindcss.com" }
        script { src = "https://unpkg.com/htmx.org@1.9.10" }
        link { href = "https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap"; rel = "stylesheet" }
        
        style {
            unsafe {
                raw("""
                    body { font-family: 'Plus Jakarta Sans', sans-serif; }

                    /* HTMX Indicator Transitions */
                    .htmx-indicator { display: none; opacity: 0; transition: opacity 250ms ease-in-out; }
                    .htmx-request .htmx-indicator, .htmx-request.htmx-indicator { display: grid; opacity: 1; }
                    
                    /* Custom Keyframe Animations */
                    @keyframes slideUpFade {
                        from { opacity: 0; transform: translateY(20px) scale(0.98); }
                        to { opacity: 1; transform: translateY(0) scale(1); }
                    }
                    @keyframes floatSlow {
                        0%, 100% { transform: translateY(0px) rotate(0deg); }
                        50% { transform: translateY(-10px) rotate(2deg); }
                    }
                    @keyframes pulseGlow {
                        0%, 100% { opacity: 0.3; transform: scale(1); }
                        50% { opacity: 0.7; transform: scale(1.05); }
                    }

                    .animate-slide-up { animation: slideUpFade 0.45s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
                    .animate-float { animation: floatSlow 6s ease-in-out infinite; }
                    .animate-pulse-glow { animation: pulseGlow 4s ease-in-out infinite; }
                    
                    /* Stagger Delays */
                    .delay-100 { animation-delay: 100ms; }
                    .delay-200 { animation-delay: 200ms; }
                    .delay-300 { animation-delay: 300ms; }
                    .delay-400 { animation-delay: 400ms; }
                """)
            }
        }

        // Global Shortcut Keyboard Script (Ctrl+K / Cmd+K)
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
    body("bg-slate-950 text-slate-100 min-h-screen relative overflow-x-hidden selection:bg-indigo-500 selection:text-white") {
        
        // Ambient background glowing orbs
        div("fixed -top-40 -left-40 w-96 h-96 bg-indigo-600/20 rounded-full blur-3xl pointer-events-none animate-pulse-glow")
        div("fixed top-1/3 -right-40 w-96 h-96 bg-cyan-500/15 rounded-full blur-3xl pointer-events-none animate-float")
        div("fixed -bottom-40 left-1/3 w-96 h-96 bg-purple-600/15 rounded-full blur-3xl pointer-events-none animate-pulse-glow")

        // Navbar dengan Glassmorphism & Keyboard Shortcut Badge
        nav("sticky top-0 z-50 backdrop-blur-xl bg-slate-900/70 border-b border-slate-800/80 transition-all duration-300") {
            div("container mx-auto px-6 py-4 flex items-center justify-between") {
                div("flex items-center gap-3 group cursor-pointer") {
                    div("w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-500 to-cyan-400 flex items-center justify-center text-xl shadow-lg shadow-indigo-500/30 group-hover:scale-110 group-hover:rotate-6 transition-all duration-300") {
                        +"✈️"
                    }
                    span("text-2xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-white via-slate-200 to-indigo-300 tracking-tight") { 
                        +"TravelWise" 
                    }
                }
                
                div("flex items-center gap-3") {
                    div("hidden sm:flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-800/80 border border-slate-700/80 text-xs font-mono text-slate-400") {
                        span("px-1.5 py-0.5 rounded bg-slate-900 border border-slate-700 text-slate-300") { +"Ctrl" }
                        span { +"+" }
                        span("px-1.5 py-0.5 rounded bg-slate-900 border border-slate-700 text-slate-300") { +"K" }
                        span("ml-1 text-slate-500 font-sans") { +"Fokus Cari" }
                    }
                    span("inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-semibold") {
                        span("relative flex h-2 w-2") {
                            span("animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75")
                            span("relative inline-flex rounded-full h-2 w-2 bg-emerald-500")
                        }
                        +"Engine Active"
                    }
                }
            }
        }
        
        div("container mx-auto px-4 py-8 md:py-14 relative z-10") {
            content()
        }

        footer("mt-20 py-8 text-center text-slate-500 text-sm border-t border-slate-800/60 backdrop-blur-md bg-slate-950/40") {
            +"© 2026 TravelWise. Interactive UI Engine with Ktor & HTMX."
        }
    }
}