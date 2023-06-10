package com.youngsdeveloper.bus_murcia.adapters

import com.youngsdeveloper.bus_murcia.models.ArrivalRealTime

interface IArrivalsAdapter {

    fun getArrivalsRealTime():List<ArrivalRealTime>;



}