package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Hour
import com.juice_studio.busmurciaapp.models.Place
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_hour.view.*
import kotlinx.android.synthetic.main.item_place.view.*
import kotlinx.android.synthetic.main.item_stop.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat

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
    }

    override fun getItemCount(): Int = items.size

}


interface PlaceClickListener {
    fun onPlaceClick(place:Place)
}


class PlaceViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val button_place:MaterialButton = itemView.button_place
}
