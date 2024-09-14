package com.example.easypropertydealer

data class CountryResponse(
    val error: Boolean,
    val msg: String,
    val data: List<CountryData>
)