package com.youngsdeveloper.bus_murcia.adapters

import com.youngsdeveloper.bus_murcia.models.*
import org.apache.xalan.xsltc.compiler.util.Type.Int
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class PanelTMPArrivalsAdapter:IArrivalsAdapter {

    var items: List<PanelItem> = mutableListOf();

    constructor(items: List<PanelItem>) {
        this.items = items
    }


    override fun getArrivalsRealTime(): List<ArrivalRealTime> {

        val arrivals = mutableListOf<ArrivalRealTime>()

        items.forEach { item ->


            var delayString = "Retrasado"
            if(item.delay_flag!="+"){
                delayString="Adelantado";
            }
            val realTimeHour = RealTimeHour(item.line_id, item.stop_id, item.synoptic,delayString,item.delay_minutes,"",0,item.destination_id,item.destination_name);
            var arrival = ArrivalRealTime(item.line_id,item.synoptic,item.destination_name, item.expected_arrival_in/60,realTimeHour)
            arrivals.add(arrival)


        }

        return arrivals.sortedBy{it.minutes}
    }


}