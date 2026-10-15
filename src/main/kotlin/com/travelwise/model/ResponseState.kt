package com.travelwise.model

/**
 * Pola arsitektur *Sealed Class* untuk memodelkan status siklus hidup respons data asynchronous.
 *
 * Menerapkan pola *State Pattern* / *Algebraic Data Type* yang type-safe untuk menangani 3 kondisi
 * dalam pemrosesan UI atau komunikasi jaringan: Sukses, Gagal, dan Sedang Memuat (*Loading*).
 *
 * @param T Tipe data konten payload yang dimuat saat status berhasil.
 */
sealed class ResponseState<out T> {

    /**
     * Kondisi ketika permintaan data berhasil diselesaikan secara sukses.
     *
     * @property data Objek hasil pengolahan data bertipe [T].
     */
    data class Success<out T>(val data: T) : ResponseState<T>()

    /**
     * Kondisi ketika terjadi kesalahan atau kegagalan saat proses pemanggilan/pemrosesan data.
     *
     * @property message Pesan deskriptif mengenai penyebab kesalahan atau error yang terjadi.
     */
    data class Error(val message: String) : ResponseState<Nothing>()

    /**
     * Kondisi ketika operasi asinkron sedang berlangsung dan data belum siap ditampilkan.
     */
    object Loading : ResponseState<Nothing>()
}
