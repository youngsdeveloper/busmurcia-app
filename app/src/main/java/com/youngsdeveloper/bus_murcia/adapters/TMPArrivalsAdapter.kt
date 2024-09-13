package com.youngsdeveloper.bus_murcia.adapters

import com.youngsdeveloper.bus_murcia.models.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TMPArrivalsAdapter:IArrivalsAdapter {

    var stop: Stop;

    constructor(stop: Stop) {
        this.stop = stop
    }

    override fun getArrivalsRealTime(): List<ArrivalRealTime> {

        val arrivals = mutableListOf<ArrivalRealTime>()

        val lines = stop.lines

        lines?.forEach { line ->


            if(line.realtime!=null){



                for(rt in line.realtime!!){
                    var LATBUS_MODE = false // If minutes latbus <= 3, use latbus info
                    var LATBUS_MINUTES: Int = 0

                    var base_time = Date()
                    if(rt!!.real_time.matches(".*\\d.*".toRegex())){
                        val calendar = Calendar.getInstance()
                        calendar.time = base_time
                        val minutes = rt.real_time.filter { it.isDigit() }
                        calendar.add(Calendar.MINUTE, minutes.toInt())
                        base_time = calendar.time

                        if(minutes.toInt() <= 3 ){
                            LATBUS_MODE = true
                            LATBUS_MINUTES = minutes.toInt()
                        }
                    }

                    val nearest_hour = line.findNearestHour(base_time)

                    nearest_hour?.let { nearest_hour ->

                        //Calculamos la hora estimada de llegada

                        var estimated_hour = nearest_hour.date

                        if (LATBUS_MODE) {
                            // Usando informacion latbus cuando minutos <= 3
                            estimated_hour = Date()
                            var cal = Calendar.getInstance()
                            cal.time = estimated_hour

                            cal.add(Calendar.MINUTE, LATBUS_MINUTES)
                            cal.set(Calendar.SECOND, 0) // Set seconds to 0
                            estimated_hour = cal.time


                        } else {
                            // Algoritmo propio
                            estimated_hour = nearest_hour.date
                            var cal = Calendar.getInstance()
                            cal.time = estimated_hour

                            if (rt.isAdelantado()) {
                                cal.add(
                                    Calendar.MINUTE,
                                    rt.delay_minutes.toInt() * (-1)
                                )
                            } else if (rt.isRetrasado()) {
                                cal.add(Calendar.MINUTE, rt.delay_minutes.toInt())
                            }
                            cal.set(Calendar.SECOND, 0) // Set seconds to 0
                            estimated_hour = cal.time

                        }

                        val diff = estimated_hour.time - Date().time
                        val diffInMinutes = (TimeUnit.MILLISECONDS.toMinutes(diff) + 1).toInt()

                        var arrival = ArrivalRealTime(line.route,line.synoptic,line.headsign, diffInMinutes,rt)
                        arrivals.add(arrival)

                    }



                }


            }
        }

        return arrivals.sortedBy{it.minutes}
    }


}