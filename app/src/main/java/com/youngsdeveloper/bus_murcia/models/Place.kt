package com.youngsdeveloper.bus_murcia.models

import android.os.Parcelable
import com.youngsdeveloper.bus_murcia.local.PlaceEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
        val id:Int,
    val name: String,
    val lat: Double,
    val lon: Double,
):Parcelable



fun Place.toPlaceEntity(): PlaceEntity {
    return PlaceEntity(this.name, this.lat, this.lon)
}
