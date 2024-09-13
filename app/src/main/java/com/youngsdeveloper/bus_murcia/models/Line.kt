package com.youngsdeveloper.bus_murcia.models

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.math.abs

@Parcelize
data class Line(
    val id: String,
    val name: String,
    val route: Int,
    val synoptic: String,
    val headsign: String,
    val hours: List<String>,
    var realtime: List<RealTimeHour?>?,
    var direction: Int=-1
):Parcelable{
    fun getHumanLineName():String{
        return "$route$synoptic"
    }


    fun getLineHours():List<Hour> {

        val line_hours = mutableListOf<Hour>();

        for(h in hours){
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, h.take(2).toInt())
            cal.set(Calendar.MINUTE, h.subSequence(3,5).toString().toInt())

            val hour = Hour(h, synoptic, cal.time)
            line_hours.add(hour)
        }

        val hourComparator = Comparator {h1: Hour, h2: Hour ->  h1.date.compareTo(h2.date)}
        line_hours.sortWith(hourComparator)
        return line_hours
    }
    fun findNearestHour(timeBase: Date):Hour?{
                var min:Long?=null
        var min_hour: Hour? = null

        Log.d("hours (filtered)", hours.toString())

        val line_hours = getLineHours()
        for(hour in line_hours){
            val diff = abs(timeBase.time - hour.date.time);
            if(min == null || diff<min){
                min = diff
                min_hour = hour
            }
        }
        return min_hour
    }
}
