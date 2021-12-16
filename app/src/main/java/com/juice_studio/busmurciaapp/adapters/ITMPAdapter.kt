package com.juice_studio.busmurciaapp.adapters

import com.juice_studio.busmurciaapp.models.Hour
import com.juice_studio.busmurciaapp.models.RealTimeData
import com.juice_studio.busmurciaapp.models.RealTimeHour

interface ITMPAdapter {

    fun getNextHours():List<Hour>;
    fun getFullHours():List<Hour>;

    fun getRealTimeData():RealTimeData;

    var activeSynoptics: List<String>
    var realtime_hours: List<RealTimeHour>
}