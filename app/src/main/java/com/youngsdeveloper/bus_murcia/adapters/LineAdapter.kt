package com.youngsdeveloper.bus_murcia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.models.Line

class LineAdapter(var items: List<Line>): RecyclerView.Adapter<LineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_line, parent, false)
        return LineViewHolder(view)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {

        val line = items[position]
        holder.text_line.text = line.getHumanLineName()

    }

    override fun getItemCount(): Int = items.size

}


class LineViewHolder(val itemView: View): RecyclerView.ViewHolder(itemView){
    val text_line: TextView

    init {
        text_line = itemView.findViewById(R.id.text_line)
    }
}
