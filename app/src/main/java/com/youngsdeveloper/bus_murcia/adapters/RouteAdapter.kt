package com.youngsdeveloper.bus_murcia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop

class RouteAdapter(var items: List<Route>, var routeClickListener: RouteClickListener): RecyclerView.Adapter<RouteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    var stop:Stop? = null

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {

        val route = items[position]
        holder.text_route.text = route.getRouteNameForHumans()
        holder.item_route_contaner.setOnClickListener {
            routeClickListener.onRouteClick(route, stop)
        }

    }

    override fun getItemCount(): Int = items.size

}


interface RouteClickListener {
    fun onRouteClick(route: Route, stop: Stop?)
}


class RouteViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_route: TextView
    val item_route_contaner: ConstraintLayout

    init {
        text_route = itemView.findViewById(R.id.text_route)
        item_route_contaner = itemView.findViewById(R.id.item_route_contaner)
    }
}
