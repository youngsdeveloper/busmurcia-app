package com.youngsdeveloper.bus_murcia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlinx.android.synthetic.main.item_stop.view.*
import kotlin.streams.toList

class StopAdapter(var items: List<Stop>, var routeClickListener: RouteClickListener): RecyclerView.Adapter<StopViewHolder>() {

    lateinit var ctx:Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {

        val stop = items[position]
        holder.text_parada.text = stop.name



        val routesSorted = stop.getRoutes().stream().sorted { r1, r2 -> r1.id.compareTo(r2.id) }.toList()

        val routeAdapter = RouteAdapter(routesSorted,routeClickListener)
        routeAdapter.stop = stop
        holder.recycler_lines.addItemDecoration(DividerItemDecoration(ctx, 0))

        holder.recycler_lines.adapter = routeAdapter
    }

    override fun getItemCount(): Int = items.size

}


class StopViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_parada:TextView = itemView.text_parada
    val recycler_lines:RecyclerView= itemView.recycler_lines

}
