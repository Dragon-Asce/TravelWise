package com.travelwise.plugins

import io.ktor.server.application.*

/**
 * Mengonfigurasi plugin serialisasi konten pada server Ktor.
 *
 * Modul ekstensi ini dipersiapkan untuk memfasilitasi negosiasi konten (*Content Negotiation*)
 * dan serialisasi JSON dua arah apabila aplikasi memperluas antarmuka untuk mengekspos
 * RESTful JSON API secara murni ke klien eksternal atau aplikasi seluler (*mobile app*).
 *
 * @author Farhan
 */
fun Application.configureSerialization() {
    // Konfigurasi plugin serialisasi server jika diperlukan di masa mendatang
}
