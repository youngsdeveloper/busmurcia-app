package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Line(
        val id: String,
        val name: String,
        val route: Int,
        val synoptic: String,
        val headsign: String,
        val hours: List<String>,
        val realtime: List<RealTimeHour>?,
        var direction: Int=-1
): Parcelable{
    fun getHumanLineName():String{
        return "$route$synoptic"
    }
}
