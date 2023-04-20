package com.youngsdeveloper.bus_murcia.adapters

import android.util.Log
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.extensions.pluralize
import com.youngsdeveloper.bus_murcia.models.Hour
import com.youngsdeveloper.bus_murcia.models.RealTimeData
import com.youngsdeveloper.bus_murcia.models.RealTimeHour
import com.youngsdeveloper.bus_murcia.models.Route
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TMPAdapter:ITMPAdapter {

    var route:Route;
    override var from_origin:Int?=null
    override var to_destination:Int?=null
    override var only_route:Int?=null

    override var activeSynoptics = listOf<String>()
    override var realtime_hours = listOf<RealTimeHour>()

    constructor(route: Route) {
        this.route = route
    }

    override fun getNextHours(): List<Hour> {
        var hours = getFullHours()


        //Obtenemos las proximas horas. (tambien las antiguas con un margen de 20min por posibles retrasos
        val now = Calendar.getInstance()
        now.add(Calendar.MINUTE, -20)
        hours = hours.filter { hour -> hour.date.after(now.time) }
        return hours
    }

    override fun getFullHours(): List<Hour> {
        var hours = route.getLineHours()
        hours = hours.filter { hour -> activeSynoptics.contains(hour.synoptic) } // Elimina horarios que no esten en active synoptics
        return hours
    }

    fun getRealTimeHours():List<RealTimeHour>{
        realtime_hours = realtime_hours.filter { rt -> activeSynoptics.contains(rt.synoptic) }


        if(from_origin!=null){
            realtime_hours = realtime_hours.filter { rt -> rt.origin_id.toInt()==from_origin}
            Log.d("inside","from_origin:" + from_origin.toString())
        }

        if(to_destination!=null){
            realtime_hours = realtime_hours.filter { rt -> rt.destination_id.toInt()==to_destination}
            Log.d("inside","to_dest: " + to_destination.toString())
        }

        if(only_route!=null){
            realtime_hours = realtime_hours.filter { rt -> rt.line_id.toInt()==only_route}
            Log.d("inside","only_route")
        }

        Log.d("hours", realtime_hours.toString())

        return realtime_hours
    }


    override fun getRealTimeData():RealTimeData {

        Log.d("Stop: " , getRealTimeHours().toString())

        var realTimeData = RealTimeData("No hay información en tiempo real",R.color.black, "", "")

        realTimeData.status_min = "-";

        var realtime_hours = getRealTimeHours()

        var min_nearest_hour: Hour? = null
        var min_realtime: RealTimeHour? = null

        var LATBUS_MODE = false // If minutes latbus <= 3, use latbus info
        var LATBUS_MINUTES: Int = 0

        for(realtime in realtime_hours){

            var base_time = Date()
            if(realtime!!.real_time.matches(".*\\d.*".toRegex())){
                val calendar = Calendar.getInstance()
                calendar.time = base_time
                val minutes = realtime.real_time.filter { it.isDigit() }
                calendar.add(Calendar.MINUTE, minutes.toInt())
                base_time = calendar.time

                if(minutes.toInt() <= 3 ){
                    LATBUS_MODE = true
                    LATBUS_MINUTES = minutes.toInt()
                }

            }


            val nearest_hour = route.findNearestHour(realtime.synoptic, base_time)

            if(min_nearest_hour==null){
                min_nearest_hour = nearest_hour
                min_realtime = realtime
            }else if(nearest_hour!!.date.before(min_nearest_hour.date)){
                min_nearest_hour = nearest_hour
                min_realtime = realtime
            }

        }

        min_nearest_hour?.let { nearest_hour ->

            realTimeData.linea_cabecera = "L" + min_realtime!!.line_id + "-" + min_realtime!!.synoptic

            //Calculamos la hora estimada de llegada

            var estimated_hour = nearest_hour.date

            if(LATBUS_MODE){
                // Usando informacion latbus cuando minutos <= 3
                estimated_hour = Date()
                var cal = Calendar.getInstance()
                cal.time = estimated_hour

                cal.add(Calendar.MINUTE, LATBUS_MINUTES)
                cal.set(Calendar.SECOND, 0) // Set seconds to 0
                estimated_hour = cal.time


            }else{
                // Algoritmo propio
                estimated_hour = nearest_hour.date
                var cal = Calendar.getInstance()
                cal.time = estimated_hour

                if(min_realtime!!.isAdelantado()){
                    cal.add(Calendar.MINUTE, min_realtime!!.delay_minutes.toInt() * (-1))
                }else if(min_realtime!!.isRetrasado()){
                    cal.add(Calendar.MINUTE, min_realtime!!.delay_minutes.toInt())
                }
                cal.set(Calendar.SECOND, 0) // Set seconds to 0
                estimated_hour = cal.time

            }

            // Calculamos la diferencia de minutos con respecto al tiempo actual

            val format: DateFormat = SimpleDateFormat("HH:mm")


            val diff = estimated_hour.time - Date().time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) + 1

            realTimeData.llegada_estimada = "Llegada estimada: " + format.format(estimated_hour)
            //text_next_bus.text = nearest_hour?.hour_string + " " + nearest_hour.synoptic + "\n" + realtime.delay_string.capitalize() + " " +realtime.delay_minutes +  " minutos" + "\n" + realtime.real_time


            if(diffInMinutes>=1){
                realTimeData.llegada_estimada = realTimeData.llegada_estimada + " (En " + diffInMinutes + " minuto".pluralize(diffInMinutes.toInt()) + ")"

                realTimeData.status_min = "$diffInMinutes min.";
            }

            //
            val status_latbus = if (min_realtime!!.real_time.matches(".*\\d.*".toRegex())) "MINUTES" else "INFO"

            if(status_latbus == "MINUTES" && diffInMinutes<=1){
                realTimeData.status = "Llegada inminente"
                realTimeData.llegada_estimada = ""
                realTimeData.status_color = R.color.green_success;
                realTimeData.status_min = "1 min.";

            }else if (status_latbus == "MINUTES"){
                if(min_realtime!!.isEnHora()){
                    realTimeData.status = "En hora"
                    realTimeData.status_color = R.color.green_success;
                }else{
                    realTimeData.status = min_realtime!!.delay_string.capitalize() + " " + min_realtime!!.delay_minutes + " " + "minuto".pluralize(min_realtime!!.delay_minutes.toInt())
                    realTimeData.status_color = R.color.red_danger
                }
            }else{
                realTimeData.llegada_estimada = ""
                if(min_realtime!!.real_time.contains("inminente")){
                    realTimeData.status_min = "Inminente";
                }else{
                    realTimeData.status_min = min_realtime!!.real_time;
                }
                realTimeData.status = min_realtime!!.real_time
                realTimeData.status_color = R.color.green_success
            }

        }

        return realTimeData
    }






}