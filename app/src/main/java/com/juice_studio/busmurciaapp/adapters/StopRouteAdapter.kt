package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.*
import kotlinx.android.synthetic.main.item_stop_route.view.*

class StopRouteAdapter(var items: List<Stop>): RecyclerView.Adapter<StopRouteViewHolder>() {


    var placeClickListener: PlaceClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopRouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop_route, parent, false)
        return StopRouteViewHolder(view)
    }


    override fun onBindViewHolder(holder: StopRouteViewHolder, position: Int) {

        val stop = items[position]

        holder.text_stop.text = stop.name
        holder.text_location.text = stop.city


    }

    override fun getItemCount(): Int = items.size

}



class StopRouteViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_stop:MaterialTextView = itemView.text_stop
    val text_location:MaterialTextView = itemView.text_location

}
