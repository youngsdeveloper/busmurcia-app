package com.youngsdeveloper.bus_murcia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Hour
import java.text.DateFormat
import java.text.SimpleDateFormat

class HourAdapter(var items: List<Hour>): RecyclerView.Adapter<HourViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {

        val hour = items[position]

        val format: DateFormat = SimpleDateFormat("HH:mm")
        holder.text_hour.text = format.format(hour.date) + " " + hour.synoptic
    }

    override fun getItemCount(): Int = items.size

}


class HourViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_hour:TextView

    init{
        text_hour = itemView.findViewById(R.id.text_hour)
    }
}
