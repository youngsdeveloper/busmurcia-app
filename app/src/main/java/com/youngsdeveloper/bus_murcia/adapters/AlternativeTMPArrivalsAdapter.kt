package com.youngsdeveloper.bus_murcia.adapters

import com.youngsdeveloper.bus_murcia.models.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlternativeTMPArrivalsAdapter:IArrivalsAdapter {

    var realTimeHours: List<RealTimeHour> = mutableListOf();

    constructor(realTimeHours: List<RealTimeHour>) {
        this.realTimeHours = realTimeHours
    }

    override fun getArrivalsRealTime(): List<ArrivalRealTime> {

        val arrivals = mutableListOf<ArrivalRealTime>()

        realTimeHours.forEach { realTimeHour ->
            if(realTimeHour.real_time.isNotBlank() && !realTimeHour.real_time.isNullOrEmpty()){

                var minutes = 1

                if(realTimeHour.real_time.startsWith("En")){
                    minutes = realTimeHour.real_time.filter {  it.isDigit() }.toInt()
                }

                var arrival = ArrivalRealTime(realTimeHour.line_id.toInt(),realTimeHour.synoptic,realTimeHour.destination_name, minutes,realTimeHour)
                arrivals.add(arrival)

            }

        }

        return arrivals.sortedBy{it.minutes}
    }


}