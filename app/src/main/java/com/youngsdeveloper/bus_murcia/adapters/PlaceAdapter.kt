package com.youngsdeveloper.bus_murcia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Place
import kotlinx.android.synthetic.main.item_place.view.*

class PlaceAdapter(var items: List<Place>): RecyclerView.Adapter<PlaceViewHolder>() {


    var placeClickListener: PlaceClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }


    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {

        val place = items[position]

        holder.button_place.text = place.name
        holder.button_place.setOnClickListener {
            placeClickListener?.let { placeClickListener -> placeClickListener.onPlaceClick(place) }
        }

        holder.button_place.setOnLongClickListener {
            placeClickListener?.let { placeClickListener -> placeClickListener.onPlaceDelete(place) }
            true
        }
    }

    override fun getItemCount(): Int = items.size

}


interface PlaceClickListener {
    fun onPlaceClick(place:Place)
    fun onPlaceDelete(place: Place)
}


class PlaceViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val button_place:MaterialButton = itemView.text_headsign
}
