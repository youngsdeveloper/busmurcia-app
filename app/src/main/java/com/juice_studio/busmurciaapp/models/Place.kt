package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import com.juice_studio.busmurciaapp.local.PlaceEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    val name: String,
    val lat: Double,
    val lon: Double,
):Parcelable



fun Place.toPlaceEntity(): PlaceEntity {
    return PlaceEntity(this.name, this.lat, this.lon)
}
