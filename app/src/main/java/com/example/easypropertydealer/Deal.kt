package com.example.easypropertydealer

data class Deal(
    var id:Int,
    var contactId: Int,
    var finalSalePrice: Double,
    var agreementDate: String,
    var documentsTransfered: Boolean,
    var note:String?=null,
    var commissionFromBuyer: Double? = null,
    var commissionFromSeller: Double? = null
)
