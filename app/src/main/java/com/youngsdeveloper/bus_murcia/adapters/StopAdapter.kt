package com.youngsdeveloper.bus_murcia.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlin.streams.toList

class StopAdapter(var items: List<Stop>, var routeClickListener: RouteClickListener): RecyclerView.Adapter<StopViewHolder>() {

    lateinit var ctx:Context

    var stopOpenClickListener :StopOpenClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {

        val stop = items[position]
        holder.text_parada.text = stop.name



        val routesSorted = stop.getRoutes().sortedBy { r -> r.id }

        val routeAdapter = RouteAdapter(routesSorted,routeClickListener)
        routeAdapter.stop = stop
        holder.recycler_lines.addItemDecoration(DividerItemDecoration(ctx, 0))

        holder.recycler_lines.adapter = routeAdapter

        holder.button_open.setOnClickListener {
            stopOpenClickListener?.let { stopOpenClickListener ->
                stopOpenClickListener.onStopOpenClick(stop)
            }
        }
    }

    override fun getItemCount(): Int = items.size

}


class StopViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_parada:TextView
    val recycler_lines:RecyclerView
    val button_open:MaterialButton

    init {
        text_parada = itemView.findViewById(R.id.text_parada)
        recycler_lines = itemView.findViewById(R.id.recycler_lines)
        button_open = itemView.findViewById(R.id.button_open)

    }
}


interface StopOpenClickListener {
    fun onStopOpenClick(stop: Stop?)
}
