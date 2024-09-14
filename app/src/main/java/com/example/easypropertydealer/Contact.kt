package com.example.easypropertydealer

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField



data class Contact(
    val id: Int,
    val name: String,
    val mobileNo: String,
    val address: String?,
    val dealer: Boolean,
    val investor: Boolean,
    val companyName: String?,
    val personDetails: String?,
    val email: String?,
    val website: String?
)

