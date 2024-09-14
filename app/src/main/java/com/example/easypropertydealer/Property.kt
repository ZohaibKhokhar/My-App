package com.example.easypropertydealer

data class Property(

    val id: Int = 0,

    val contactId: Int? = null,

    val purpose: String? = null,

    val propertyType: String? = null,

    val area: Int? = null,

    val areaUnit: String? = null,

    val locationId:Int? = null,

    val estimatedDemand: Double? = null,

    val note: String? = null,

    val completeAddress: String? = null,

    val plotNo: String? = null,

    val block: String? = null,

    val phase: String? = null,

)
