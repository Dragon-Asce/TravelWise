package com.travelwise.model

/**
 * Tingkatan anggaran (*budget tier*) yang dapat dipilih oleh pengguna untuk merencanakan liburan.
 *
 * Masing-masing tingkatan memiliki alokasi acuan harian dalam satuan USD (Dolar Amerika)
 * serta deskripsi gaya perjalanan yang disediakan.
 *
 * @property label Nama tampilan ramah pengguna untuk tingkatan anggaran (misal: "Backpacker").
 * @property dailyUsd Estimasi patokan biaya harian dalam satuan USD.
 * @property description Deskripsi karakteristik akomodasi, konsumsi, dan transportasi pada tingkatan ini.
 */
enum class BudgetTier(val label: String, val dailyUsd: Double, val description: String) {
    /** Gaya perjalanan hemat: menginap di hostel, konsumsi street food, dan menggunakan transportasi publik. */
    BACKPACKER("Backpacker", 40.0, "Hostel, street food, transportasi umum"),

    /** Gaya perjalanan menengah: menginap di hotel bintang 3, makan di restoran lokal, transportasi campuran. */
    MODERATE("Moderate", 120.0, "Hotel bintang 3, restoran lokal, transport campuran"),

    /** Gaya perjalanan mewah: menginap di hotel bintang 5, fine dining, dan transportasi privat. */
    LUXURY("Luxury", 350.0, "Hotel bintang 5, fine dining, transport pribadi");

    companion object {
        /**
         * Mengonversi string parameter masukan menjadi objek [BudgetTier].
         *
         * Bersifat toleran terhadap variasi huruf besar/kecil (*case-insensitive*) dan spasi kosong.
         * Jika parameter kosong (*null*) atau tidak cocok dengan opsi yang ada, akan mengembalikan [MODERATE] sebagai nilai bawaan (*default*).
         *
         * @param value Nama enum atau string tingkatan anggaran yang dikirim dari form/query parameter.
         * @return Objek [BudgetTier] yang sesuai atau [MODERATE] jika tidak valid.
         */
        fun fromParam(value: String?): BudgetTier =
            values().firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: MODERATE
    }
}

/**
 * Rincian pos pengeluaran spesifik dalam satu hari perjalanan (misal: Akomodasi, Konsumsi, Transportasi).
 *
 * @property label Nama pos pengeluaran dalam bahasa Indonesia (contoh: "Akomodasi", "Makan & minum").
 * @property usd Nominal biaya dalam satuan USD.
 * @property local Nominal biaya dalam valuta lokal negara tujuan (bisa bernilai `null` jika kurs tidak ditemukan).
 */
data class BudgetLine(
    val label: String,
    val usd: Double,
    val local: Double?
)

/**
 * Rencana anggaran komprehensif harian untuk destinasi yang dituju.
 *
 * Menggabungkan informasi tingkatan anggaran terpilih, total pengeluaran harian
 * dalam USD, valuta lokal negara tujuan, dan mata uang Rupiah (IDR),
 * serta rincian persentase pembagian pos biaya.
 *
 * @property tier Tingkatan anggaran yang diterapkan ([BudgetTier]).
 * @property dailyUsd Total estimasi biaya harian dalam satuan USD.
 * @property localCurrency Kode 3-huruf mata uang resmi negara tujuan (contoh: "JPY", "EUR", "IDR").
 * @property dailyLocal Total estimasi biaya harian dalam mata uang lokal negara tujuan.
 * @property dailyIdr Total estimasi biaya harian yang telah dikonversi ke Rupiah (IDR).
 * @property localPerUsd Nilai tukar 1 USD terhadap mata uang lokal negara tujuan.
 * @property breakdown Daftar rincian komponen alokasi pengeluaran harian ([BudgetLine]).
 */
data class BudgetPlan(
    val tier: BudgetTier,
    val dailyUsd: Double,
    val localCurrency: String,
    val dailyLocal: Double?,
    val dailyIdr: Double?,
    val localPerUsd: Double?,
    val breakdown: List<BudgetLine>
)