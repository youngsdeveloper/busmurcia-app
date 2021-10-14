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
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_route.*


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

                val synopsis = mutableListOf<String>()
                for(chip in chipList){
                    if(chip.isChecked){
                        val synoptic = chip.tag as String
                        synopsis.add(synoptic)
                    }
                }

                hours_adapter.items = route.getLineHoursBySinoptic(synopsis)
                hours_adapter.notifyDataSetChanged()


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


        chip_group_synoptic.setOnCheckedChangeListener { group, checkedId ->
            Toast.makeText(requireActivity(), "Hola!", Toast.LENGTH_LONG).show();
        }

        val hours = route.getLineHoursBySinoptic(route.getSynopticInRoute())

        hours_adapter = HourAdapter(hours)
        recycler_hours.adapter = hours_adapter

        // initialize grid layout manager
        GridLayoutManager(
            requireContext(), // context
            4, // span count
            RecyclerView.VERTICAL, // orientation
            false // reverse layout
        ).apply {
            // specify the layout manager for recycler view
            recycler_hours.layoutManager = this
        }

    }
}