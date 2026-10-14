package com.travelwise.model

enum class BudgetTier(val label: String, val dailyUsd: Double, val description: String) {
    BACKPACKER("Backpacker", 40.0, "Hostel, street food, transportasi umum"),
    MODERATE("Moderate", 120.0, "Hotel bintang 3, restoran lokal, transport campuran"),
    LUXURY("Luxury", 350.0, "Hotel bintang 5, fine dining, transport pribadi");

    companion object {
        fun fromParam(value: String?): BudgetTier =
            values().firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: MODERATE
    }
}

data class BudgetLine(val label: String, val usd: Double, val local: Double?)

data class BudgetPlan(
    val tier: BudgetTier,
    val dailyUsd: Double,
    val localCurrency: String,
    val dailyLocal: Double?,
    val dailyIdr: Double?,
    val localPerUsd: Double?,
    val breakdown: List<BudgetLine>
)