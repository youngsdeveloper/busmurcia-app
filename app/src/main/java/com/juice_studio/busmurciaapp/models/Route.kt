package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Route(
    val id:Int,
    val lines: List<Line>
): Parcelable{
    fun getRouteNameForHumans():String{
        return "L$id - ${getRouteHeadsign()}"
    }

    fun getRouteHeadsign():String{
        return lines[0].headsign
    }

    fun getSynopticInRoute():List<String>{
        return lines.map{line -> line.synoptic}.distinct()
    }

    fun getLinesBySinoptic(synoptics:List<String>):List<Line>{
        return lines.filter { line -> synoptics.contains(line.synoptic) }
    }

    fun getLineHoursBySinoptic(synoptics:List<String>):List<String>{

        val line_hours = mutableListOf<String>();

        for(line in getLinesBySinoptic(synoptics)){
            for(hour in line.hours){
                line_hours.add("$hour${line.synoptic}")
            }
        }

        return line_hours
    }
}
