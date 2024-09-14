package com.example.easypropertydealer


data class CountryData(
    val iso2: String,
    val iso3: String,
    val country: String,
    val cities: List<String>
)
