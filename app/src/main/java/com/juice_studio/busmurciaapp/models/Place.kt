package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val name: String,
    val lat: Double,
    val lon: Double,
):Parcelable
