package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_stop.view.*

class StopAdapter(var items: List<Stop>, var routeClickListener: RouteClickListener): RecyclerView.Adapter<StopViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {

        val stop = items[position]
        holder.text_parada.text = stop.name

        val routeAdapter = RouteAdapter(stop.getRoutes(),routeClickListener)
        holder.recycler_lines.adapter = routeAdapter
    }

    override fun getItemCount(): Int = items.size

}


class StopViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_parada:TextView = itemView.text_parada
    val recycler_lines:RecyclerView= itemView.recycler_lines

}