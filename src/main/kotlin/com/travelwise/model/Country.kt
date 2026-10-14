package com.travelwise.model

data class Country(
    val name: String,
    val capital: String,
    val population: Long,
    val currencyCode: String = "USD",
    val currencyName: String = "United States Dollar",
    val officialName: String = name,
    val flagUrl: String? = null
)