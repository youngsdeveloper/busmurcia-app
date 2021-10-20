package com.juice_studio.busmurciaapp.models

import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.math.abs

@Parcelize
data class Route(
        val id: Int,
        val lines: List<Line>
): Parcelable{
    fun getRouteNameForHumans():String{
        return "L$id - ${getRouteHeadsign()}"
    }

    fun getRouteHeadsign():String{
        return lines[0].headsign
    }

    fun getSynopticInRoute():List<String>{
        return lines.map{ line -> line.synoptic}.distinct()
    }

    fun getLinesBySinoptic(synoptics: List<String>):List<Line>{
        return lines.filter { line -> synoptics.contains(line.synoptic) }
    }

    fun getLineHours():List<Hour> {

        val line_hours = mutableListOf<Hour>();

        for(line in lines){
            for(h in line.hours){
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, h.take(2).toInt())
                cal.set(Calendar.MINUTE, h.subSequence(3,5).toString().toInt())

                val hour = Hour(h, line.synoptic, cal.time)
                line_hours.add(hour)
            }
        }

        val hourComparator = Comparator {h1: Hour, h2: Hour ->  h1.date.compareTo(h2.date)}
        line_hours.sortWith(hourComparator)
        return line_hours
    }

    fun getRealTimeHours():List<RealTimeHour>{
        val realtime = mutableListOf<RealTimeHour>();
        for(line in lines){
            line.realtime?.let { r -> realtime.addAll(r) }
        }
        return realtime
    }


    fun findNearestHour(synoptic:String, timeBase: Date):Hour?{
        var hours = getLineHours()
        hours = hours.filter { h -> h.synoptic.equals(synoptic) }

        var min:Long?=null
        var min_hour: Hour? = null

        for(hour in hours){
            val diff = abs(timeBase.time - hour.date.time);
            if(min == null || diff<min){
                min = diff
                min_hour = hour
            }
        }
        return min_hour
    }
}
