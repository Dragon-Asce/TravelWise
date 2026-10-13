# Panduan Kontribusi (Contributing Guidelines) - TravelWise Web App

Terima kasih telah berkontribusi dalam pengembangan **TravelWise Web App**! Panduan ini dibuat untuk memastikan alur kerja kelompok berjalan rapi, konsisten, dan mempermudah penilaian *log Git*.

---

## 1. Alur Kerja Git & Strategi Branching

Proyek ini menggunakan alur kerja **Git Flow** sederhana:

- **`main`**: Branch stabil utama. Hanya berisi kode yang sudah siap diuji/dinilai.
- **`develop`**: Branch integrasi utama tempat penggabungan fitur-fitur baru.
- **`feature/<nama-fitur>`**: Branch individu untuk setiap anggota (contoh: `feature/network-layer`, `feature/weather-widget`).

### Aturan Pembuatan Branch:
1. Selalu buat branch baru dari `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/nama-fitur
   ```

2. **Dilarang push langsung ke `main` atau `develop`** (gunakan *Pull Request*).

---

## 2. Standar Commit (Conventional Commits)

Setiap anggota wajib melakukan commit secara mandiri dengan pesan commit yang jelas dan terstruktur.

### Format Commit:

```text
<type>(<scope>): <deskripsi singkat>
```

### Tipe Commit (`type`):

* `feat`: Menambahkan fitur baru (contoh: `feat(network): implement WeatherApiClient`).
* `fix`: Memperbaiki bug (contoh: `fix(routing): handle empty country input error`).
* `docs`: Perubahan dokumentasi (contoh: `docs(readme): update setup instructions`).
* `test`: Menambah atau memperbaiki pengujian (contoh: `test(service): add unit test for TravelService`).
* `refactor`: Perapihan kode tanpa mengubah fungsionalitas (contoh: `refactor(model): optimize data classes`).
* `build` / `ci`: Perubahan konfigurasi Gradle / Docker.

---

## 3. Alur Pull Request (PR) & Code Review

1. Setelah fitur selesai dan diuji secara lokal, push branch fitur Anda:
    ```bash
    git push origin feature/nama-fitur
    ```
2. Buka **Pull Request (PR)** dari `feature/nama-fitur` menuju `develop` di GitHub.
3. Isilah deskripsi PR dengan rincian perubahan yang dilakukan.
4. Minta minimal **1 anggota tim (misal: Arya / Abdul)** untuk mereview PR Anda.
5. Setelah disetujui (*Approved*), lakukan *Merge* ke `develop`.

---

## 4. Standar Penulisan Kode (Coding Style & KDoc)

* **Bahasa & Runtime:** Kotlin (JDK 17).
* **Naming Convention:**
    * Kelas & Interface: `PascalCase` (contoh: `CountryApiClient`, `ActivityRecommender`).
    * Variabel & Fungsi: `camelCase` (contoh: `fetchCountry`, `weatherData`).
    * Konstanta: `UPPER_SNAKE_CASE` (contoh: `BASE_URL`).
* **Dokumentasi (KDoc):** Setiap kelas dan fungsi publik wajib dilengkapi dengan KDoc yang menjelaskan fungsi, parameter, dan nilai kembaliannya.

---

