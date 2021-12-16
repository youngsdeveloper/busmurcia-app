package com.juice_studio.busmurciaapp.adapters

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Hour
import com.juice_studio.busmurciaapp.models.RealTimeData
import com.juice_studio.busmurciaapp.models.RealTimeHour
import com.juice_studio.busmurciaapp.models.Route
import kotlinx.android.synthetic.main.fragment_route.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TMPAdapter:ITMPAdapter {

    var route:Route;
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
        return realtime_hours.filter { rt -> activeSynoptics.contains(rt.synoptic) }
    }


    override fun getRealTimeData():RealTimeData {

        var realTimeData = RealTimeData("No hay información en tiempo real",R.color.black, "", "")

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
                realTimeData.llegada_estimada = realTimeData.llegada_estimada + " (En " + diffInMinutes + pluralize(
                        diffInMinutes.toInt(),
                        " minuto"
                ) + ")"
            }

            //
            val status_latbus = if (min_realtime!!.real_time.matches(".*\\d.*".toRegex())) "MINUTES" else "INFO"

            if(status_latbus == "MINUTES" && diffInMinutes<=1){
                realTimeData.status = "Llegada inminente"
                realTimeData.llegada_estimada = ""
                realTimeData.status_color = R.color.green_success;
            }else if (status_latbus == "MINUTES"){
                if(min_realtime!!.isEnHora()){
                    realTimeData.status = "En hora"
                    realTimeData.status_color = R.color.green_success;
                }else{
                    realTimeData.status = min_realtime!!.delay_string.capitalize() + " " + min_realtime!!.delay_minutes + " " + pluralize(
                            min_realtime!!.delay_minutes.toInt(),
                            "minuto"
                    )
                    realTimeData.status_color = R.color.red_danger
                }
            }else{
                realTimeData.llegada_estimada = ""
                realTimeData.status = min_realtime!!.real_time
                realTimeData.status_color = R.color.green_success
            }

        }

        return realTimeData
    }




    //TODO: Crear como extension
    private fun pluralize(cant: Int, str: String):String{
        if(cant>1){
            return str + "s"
        }
        return str
    }


}