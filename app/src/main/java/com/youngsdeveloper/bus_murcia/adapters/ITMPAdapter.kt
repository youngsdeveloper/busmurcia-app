package com.youngsdeveloper.bus_murcia.adapters

import com.youngsdeveloper.bus_murcia.models.Hour
import com.youngsdeveloper.bus_murcia.models.RealTimeData
import com.youngsdeveloper.bus_murcia.models.RealTimeHour

interface ITMPAdapter {

    fun getNextHours():List<Hour>;
    fun getFullHours():List<Hour>;

    fun getRealTimeData():RealTimeData;



    var activeSynoptics: List<String>
    var realtime_hours: List<RealTimeHour>
    var from_origin: Int?
    var to_destination: Int?
    var only_route: Int?

}