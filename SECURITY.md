# Kebijakan Keamanan (Security Policy) - TravelWise Web App

## 1. Versi yang Didukung (Supported Versions)

Tabel berikut menjelaskan status dukungan pembaruan keamanan untuk setiap versi proyek TravelWise Web App:

| Versi | Didukung | Catatan |
| :--- | :---: | :--- |
| `1.0.x` (`main`) | Yes | Versi stabil utama |
| `dev` (`debug`) | No | Branch integrasi aktif |

## 2. Pelaporan Celah Keamanan (Reporting a Vulnerability)

Kami berkomitmen untuk menjaga keamanan aplikasi **TravelWise Web App**. Jika Anda menemukan celah keamanan (*vulnerability*) atau bug berisiko tinggi:

1. **JANGAN** membuat *Public Issue* di repositori GitHub ini.
2. Laporkan temuan secara privat kepada tim pengembang [**Abdul Majid**](https://github.com/figliaaa/)
3. Sertakan informasi berikut dalam laporan Anda:
   - Deskripsi celah keamanan.
   - Langkah-langkah untuk mereproduksi celah (*Proof of Concept*).
   - Dampak potensial pada aplikasi atau server.

Tim kami akan mengonfirmasi penerimaan laporan dalam waktu **24–48 jam** dan memberikan kabar berkala hingga pembaruan dirilis.

## 3. Manajemen API Key & Rahasia (Secrets Management)

Proyek ini memanfaatkan Public API eksternal (**OpenWeatherMap API** dan **REST Countries API**).

- **Dilarang keras melakukan hardcode API Key di dalam kode sumber Kotlin.**
- Semua API Key wajib dikonfigurasi melalui **Environment Variable** `OPENWEATHER_API_KEY`.
- File lokal yang berisi kredensial/kunci (seperti `.env` atau `local.properties`) wajib dimasukkan ke dalam `.gitignore`.
- Pada lingkungan Docker, API Key disuntikkan secara dinamis melalui `docker-compose.yml` tanpa menyimpan kunci di dalam *Docker Image*.

## 4. Keamanan Kontainer Docker & Dependensi

- **Minimal Base Image:** Dockerfile menggunakan *image* ringan berbasis Alpine (`eclipse-temurin:17-jre-alpine`) untuk mengurangi area potensi serangan (*attack surface*).
- **Multi-stage Build:** Proses *compile/build* dipisahkan dari lingkungan *runtime* agar biner akhir tidak membawa *build tools* yang tidak diperlukan.
- **Dependency Audit:** Pustaka pihak ketiga (Ktor, Kotlinx Serialization) diperbarui secara berkala ke versi stabil terbaru.
