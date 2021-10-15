package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.HourAdapter
import com.juice_studio.busmurciaapp.models.Hour
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_route.*
import java.util.*


class RouteFragment : Fragment(R.layout.fragment_route) {

    val args: RouteFragmentArgs by navArgs()

    private lateinit var hours_adapter :HourAdapter
    private val chipList = mutableListOf<Chip>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val route = args.route

        text_route_number.text = "L${route.id}"
        text_route_headsign.text = "${route.getRouteHeadsign()}"

        val onCheckedListener = object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                hours_adapter.items = getHoursFiltered()
                hours_adapter.notifyDataSetChanged()
                loadRealTimeData()
            }
        }

        for(synoptic in route.getSynopticInRoute()){
            val chip = layoutInflater.inflate(R.layout.layout_chip_choice, chip_group_synoptic, false) as Chip
            chip.text = "L${route.id} - ${synoptic}"
            chip.tag = synoptic
            chip.setOnCheckedChangeListener(onCheckedListener)
            chip_group_synoptic.addView(chip)
            chipList.add(chip)
        }

        loadRealTimeData()


        var hours = route.getLineHours()
        if(!switch_full_hours.isChecked){
            hours = hours.filter { hour -> hour.date.after(Date()) }
        }

        hours_adapter = HourAdapter(hours)
        recycler_hours.adapter = hours_adapter

        GridLayoutManager(
            requireContext(),
            4,
            RecyclerView.VERTICAL,
            false
        ).apply {
            recycler_hours.layoutManager = this
        }

        switch_full_hours.setOnCheckedChangeListener { buttonView, isChecked ->
            hours_adapter.items = getHoursFiltered()
            hours_adapter.notifyDataSetChanged()
        }


    }

    private fun loadRealTimeData(){
        text_next_bus.text = "No hay información en tiempo real"

        var realtime_hours = args.route.getRealTimeHours()

        realtime_hours = realtime_hours.filter { rt -> getActiveSynoptics().contains(rt.synoptic) }



        for(realtime in realtime_hours){

            val nearest_hour = args.route.findNearestHour(realtime.synoptic)
            nearest_hour?.let { nearest_hour ->
                text_next_bus.text = nearest_hour?.hour_string + " " + nearest_hour.synoptic + "\n" + realtime.delay_string.capitalize() + " " +realtime.delay_minutes +  " minutos" + "\n" + realtime.real_time
            }
        }
    }

    private fun getHoursFiltered():List<Hour>{

        var hours = args.route.getLineHours()
        hours = hours.filter { hour -> getActiveSynoptics().contains(hour.synoptic) }

        if(!switch_full_hours.isChecked){
            hours = hours.filter { hour -> hour.date.after(Date()) }
        }

        return hours
    }

    private fun getActiveSynoptics():List<String>{

        val synoptics = mutableListOf<String>()

        for(chip in chipList){

            if(chip.isChecked){

                val synoptic = chip.tag as String
                synoptics.add(synoptic)
            }
        }

        return Collections.unmodifiableList(synoptics)
    }
}