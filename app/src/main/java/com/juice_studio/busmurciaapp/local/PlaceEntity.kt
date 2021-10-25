package com.juice_studio.busmurciaapp.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.juice_studio.busmurciaapp.models.Place


@Entity
data class PlaceEntity(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "lat")
    var lat: Double,

    @ColumnInfo(name = "lon")
    var lon: Double,
){

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}




fun PlaceEntity.toPlace(): Place {
    return Place(this.name, this.lat, this.lon)
}
