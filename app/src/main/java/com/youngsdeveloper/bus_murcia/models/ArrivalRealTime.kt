package com.youngsdeveloper.bus_murcia.models

data class ArrivalRealTime(
    val route:Int,
    val synoptic:String,
    val headsign: String,
    val minutes: Int,
    val realTimeData: RealTimeHour
)
