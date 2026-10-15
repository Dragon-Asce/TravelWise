package com.travelwise.model

/**
 * Entitas domain yang merepresentasikan data geografis dan kedaulatan suatu negara destinasi.
 *
 * Data ini biasanya diperoleh dari REST Countries API v3.1/v5 atau fallback kamus lokal,
 * dan digunakan untuk memberikan konteks kedaulatan, ibu kota, mata uang, dan bendera.
 *
 * @property name Nama umum negara dalam bahasa Inggris atau hasil resolusi (contoh: "Japan", "Indonesia").
 * @property capital Nama kota ibu kota resmi negara (contoh: "Tokyo", "Jakarta").
 * @property population Jumlah estimasi populasi penduduk negara.
 * @property currencyCode Kode ISO 4217 tiga-huruf untuk mata uang resmi negara (contoh: "USD", "JPY", "IDR").
 * @property currencyName Nama lengkap mata uang resmi negara (contoh: "Japanese Yen", "Indonesian Rupiah").
 * @property officialName Nama resmi konstitusional negara (contoh: "Republic of Indonesia").
 * @property flagUrl Tautan URL berkas gambar bendera resmi negara (PNG/SVG dari FlagCDN atau REST Countries).
 */
data class Country(
    val name: String,
    val capital: String,
    val population: Long,
    val currencyCode: String = "USD",
    val currencyName: String = "United States Dollar",
    val officialName: String = name,
    val flagUrl: String? = null
)