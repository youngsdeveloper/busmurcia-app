package com.juice_studio.busmurciaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Line
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.item_line.view.*
import kotlinx.android.synthetic.main.item_route.view.*
import kotlinx.android.synthetic.main.item_stop.view.*

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
    val text_line: TextView = itemView.text_line
}
