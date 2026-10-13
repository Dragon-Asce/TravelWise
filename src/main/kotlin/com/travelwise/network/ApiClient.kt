package com.travelwise.network

/**
 * Kontrak utama untuk seluruh klien jaringan yang melakukan komunikasi HTTP ke Public API eksternal.
 *
 * Antarmuka ini menerapkan prinsip abstraksi OOP untuk memisahkan logika komunikasi
 * HTTP jaringan dari konsumen lapisan bisnis (Service Layer).
 *
 * @param T Tipe data model hasil ekstraksi JSON respons API.
 * @author Farhan
 */
interface ApiClient<T> {

    /**
     * Mengambil data dari endpoint API eksternal berdasarkan kata kunci pencarian.
     *
     * @param query Parameter pencarian utama (seperti nama negara atau nama kota).
     * @return Objek tipe [T] jika permintaan berhasil diproses, atau `null` jika terjadi kegagalan/error.
     */
    suspend fun fetchData(query: String): T?
}