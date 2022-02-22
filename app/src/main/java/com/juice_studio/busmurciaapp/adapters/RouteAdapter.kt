package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Line
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_line.view.*
import kotlinx.android.synthetic.main.item_route.view.*
import kotlinx.android.synthetic.main.item_stop.view.*

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
    val text_route: TextView = itemView.text_route
    val item_route_contaner = itemView.item_route_contaner
}
