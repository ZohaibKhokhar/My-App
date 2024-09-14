package com.example.easypropertydealer

data class Reminder(
    var id:Int,
    var reminderPurpose:String,
    var purposeId:Int,
    var about: String,
    var dateAndTime:String,
    var detail:String?=null
)
