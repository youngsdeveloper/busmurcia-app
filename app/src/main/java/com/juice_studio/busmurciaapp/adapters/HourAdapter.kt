package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_hour.view.*
import kotlinx.android.synthetic.main.item_stop.view.*

class HourAdapter(var items: List<String>): RecyclerView.Adapter<HourViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {

        holder.text_hour.text = items[position]
    }

    override fun getItemCount(): Int = items.size

}


class HourViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_hour:TextView = itemView.text_hour

}
