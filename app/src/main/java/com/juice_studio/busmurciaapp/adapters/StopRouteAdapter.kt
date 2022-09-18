package com.juice_studio.busmurciaapp.adapters

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.material.textview.MaterialTextView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.extensions.dp
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_stop_route.view.*


class StopRouteAdapter(var items: List<Stop>, var context: Context): RecyclerView.Adapter<StopRouteViewHolder>() {


    var placeClickListener: PlaceClickListener? = null
    var active_item: Stop? = null

    var recyclerView: RecyclerView? = null


    var stopClickListener: StopClickListener? = null

    var statusStops = mutableMapOf<Int, String>()

    var routesStops = mutableMapOf<Int, Route>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopRouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop_route, parent, false)
        return StopRouteViewHolder(view)
    }


    override fun onBindViewHolder(holder: StopRouteViewHolder, position: Int) {

        val stop = items[position]

        holder.text_stop.text = stop.name
        holder.text_location.text = stop.city


        var active_item_position = -1;

        if(active_item!=null){
            active_item_position = getStopIndex(active_item!!)
        }

        if(active_item_position==position){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.green_success))


            holder.text_stop.setPadding(0, 300.dp, 0, 0);
            holder.text_location.setPadding(0, 0, 0, 300.dp)


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.image_mark.setImageDrawable(context.getDrawable(R.drawable.tmp_mark))
            }

            val minutos_llegada = listOf("Inminente", "1 min.", "2 min.", "3 min.", "4 min.", "5 min.", "6 min.", "7 min.", "8 min.")


        }else{
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            holder.text_stop.setPadding(0, 0, 0, 0);
            holder.text_location.setPadding(0, 0, 0, 0)

            holder.text_next_bus_minutes.visibility = View.GONE
            holder.loading_realtime_stop.visibility = View.GONE
        }


        if(statusStops.containsKey(stop.id) && active_item_position==position){
            val status = statusStops.get(stop.id)!!
            if(!status.isEmpty()){
                holder.text_next_bus_minutes.text = status
            }else{
                holder.text_next_bus_minutes.text = "-"
            }
            holder.text_next_bus_minutes.visibility = View.VISIBLE

        }else if(active_item_position==position){
            holder.text_next_bus_minutes.text = "..." // Cargando
            holder.text_next_bus_minutes.visibility = View.VISIBLE
        }else{
            holder.text_next_bus_minutes.visibility = View.GONE
        }




        holder.container.setOnClickListener {


            if(active_item_position==position){
                // Si ya es el activo
                stopClickListener?.let { stopClickListener ->  stopClickListener.onStopDoubleClick(stop, routesStops[stop.id])}
            }else{
                loadActive(position)
            }



        }




    }

    fun getStopIndex(stop:Stop):Int{
        return items.indexOf(stop)
    }

    override fun getItemCount(): Int = items.size


    fun loadActive(name: String){
        items.firstOrNull{ stop -> stop.name == name }?.let { active -> loadActive(active) }
    }

    fun loadActive(stop: Stop){
        var index = items.indexOf(stop)

        if(index!=-1){
            loadActive(index)
        }
    }

    fun loadActive(position: Int){


        active_item?.let { active_item -> notifyItemChanged(getStopIndex(active_item))}
        active_item = items[position]
        notifyItemChanged(position)
        stopClickListener?.let { stopClickListener ->  stopClickListener.onStopClick(items[position], object : StopRealtimeListener {


            override fun onStopRealtimeLoaded(stop: Stop, status: String, route: Route) {


                statusStops[stop.id.toInt()] = status
                routesStops[stop.id.toInt()] = route
                notifyItemChanged(position, Unit)
            }

        })}


        recyclerView?.let { recyclerView ->



            Handler().postDelayed(Runnable {

                val smoothScroller: SmoothScroller = object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }

                smoothScroller.targetPosition = position;

                recyclerView.layoutManager!!.startSmoothScroll(smoothScroller);



            }, 200)
        }

    }

}


interface StopClickListener {
    fun onStopClick(stop: Stop, listener: StopRealtimeListener)
    fun onStopDoubleClick(stop: Stop, route: Route?)
}


interface StopRealtimeListener {
    fun onStopRealtimeLoaded(stop: Stop, status: String, route: Route)
}



class StopRouteViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_stop:MaterialTextView = itemView.text_stop
    val text_location:MaterialTextView = itemView.text_location
    val text_next_bus_minutes:MaterialTextView = itemView.text_next_bus_minutes

    val loading_realtime_stop:ProgressBar = itemView.loading_realtime_stop

    val image_mark = itemView.image_mark

    val container:View = itemView.item_stop_route_container

}
