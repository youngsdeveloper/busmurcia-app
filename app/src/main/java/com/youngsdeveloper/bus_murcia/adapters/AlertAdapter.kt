package com.youngsdeveloper.bus_murcia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Line
import kotlinx.android.synthetic.main.item_alert.view.*
import kotlinx.android.synthetic.main.item_line.view.*

class AlertAdapter(var items: Map<String,String>): RecyclerView.Adapter<AlertViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    var listener:AlertClickListener? = null

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {

        val new = items.keys.toList()[position]
        holder.text_alert.text = new

        holder.item_alert_contaner.setOnClickListener {
            listener?.let { listener -> listener.onAlertClicked(items[new]!!) }
        }

    }

    override fun getItemCount(): Int = items.size

}


class AlertViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_alert: TextView = itemView.text_alert
    val item_alert_contaner: View = itemView.item_alert_contaner
}

public interface AlertClickListener{
    fun onAlertClicked(codigo:String)
}