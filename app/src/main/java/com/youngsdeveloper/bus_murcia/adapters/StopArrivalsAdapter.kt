package com.youngsdeveloper.bus_murcia.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.extensions.pluralize
import com.youngsdeveloper.bus_murcia.models.ArrivalRealTime
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlin.streams.toList

class StopArrivalsAdapter(var items: List<ArrivalRealTime>): RecyclerView.Adapter<StopArrivalsViewHolder>() {

    lateinit var ctx:Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopArrivalsViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_arrival, parent, false)
        return StopArrivalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopArrivalsViewHolder, position: Int) {

        val arrival = items[position]
        val rt = arrival.realTimeData



        holder.text_route.text = "${arrival.route}-${arrival.synoptic}"

        arrival.headsign?.let {
            if(arrival.headsign.split("-> ").size>1){
                holder.text_headsign.text = arrival.headsign.split("-> ")[1]
            }else{
                holder.text_headsign.text = arrival.headsign
            }
        }






        var status = "En hora"
        if(!rt.isEnHora()){
            status = "${rt.delay_string.capitalize()} ${rt.delay_minutes} ${"minuto".pluralize(rt.delay_minutes.toInt())}"
        }

        if(rt.isEnHora()){
            holder.text_status.setTextColor(ContextCompat.getColor(ctx, R.color.green_success))
        }else{
            holder.text_status.setTextColor(ContextCompat.getColor(ctx, R.color.red_danger))
        }

        holder.text_status.text = status


        if(arrival.minutes<=1){
            holder.text_next_bus_minutes.text = "⬇️⬇️"

        }else{
            holder.text_next_bus_minutes.text = "En ${arrival.minutes} ${"minuto".pluralize(arrival.minutes)}"

        }

    }

    override fun getItemCount(): Int = items.size

}


class StopArrivalsViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_route:TextView
    val text_headsign:TextView
    val text_status:TextView
    val text_next_bus_minutes:TextView

    init {
        text_route = itemView.findViewById(R.id.text_route)
        text_headsign = itemView.findViewById(R.id.text_headsign)
        text_status = itemView.findViewById(R.id.text_status)
        text_next_bus_minutes = itemView.findViewById(R.id.text_next_bus_minutes)

    }
}


