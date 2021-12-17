package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.StopRouteAdapter
import com.juice_studio.busmurciaapp.io.ApiAdapter
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import com.juice_studio.busmurciaapp.models.StopRoute
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.android.synthetic.main.fragment_route.chip_group_synoptic
import kotlinx.android.synthetic.main.fragment_route_stops.*
import kotlinx.android.synthetic.main.fragment_route_stops.recycler_stops
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RouteStopsFragment : Fragment(R.layout.fragment_route_stops){

    private val chipList = mutableListOf<Chip>()

    val args: RouteStopsFragmentArgs by navArgs()


    lateinit var route:Route
    lateinit var stopRoutes:List<StopRoute>


    private var synoptics = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val route = args.route
        this.route = route

        this.synoptics = args.synoptics.toMutableList()

        loadSynoptic(route)
        downloadRouteStops(route);

    }

    private fun loadSynoptic(route: Route){
        val onCheckedListener = object: CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                //updateHours();
                //downloadLocalRealTimeData()

                if(isChecked){
                    synoptics.add(buttonView?.tag as String)
                }else{
                    synoptics.remove(buttonView?.tag as String)
                }

                loadRouteStop(stopRoutes);
            }
        }

        for(synoptic in route.getSynopticInRoute()){
            val chip = layoutInflater.inflate(
                    R.layout.layout_chip_choice,
                    chip_group_synoptic,
                    false
            ) as Chip
            chip.text = "L${route.id} - ${synoptic}"
            chip.tag = synoptic
            chip.isChecked = synoptics.contains(synoptic)
            chip.setOnCheckedChangeListener(onCheckedListener)
            chip_group_synoptic.addView(chip)
            chipList.add(chip)
        }
    }

    private fun downloadRouteStops(route: Route){

        CoroutineScope(Dispatchers.IO).launch {


            val stopsList = listOf<String>(args.stop.id.toString())
            val linesList = args.route.lines.map { line -> line.id }

            val call = ApiAdapter
                    .getApiService()
                    .getLineStops(route.id)



            requireActivity().runOnUiThread {

                if(call.isSuccessful){
                    val routes = call.body();
                    routes?.let { routes ->
                        loadRouteStop(routes)
                    }
                }
            }
        }
    }

    private fun loadRouteStop(routes: List<StopRoute>){

        this.stopRoutes = routes

        val sr = routes
                .filter { stopRoute ->  stopRoute.direction == route.lines[0].direction}
                .filter { stopRoute ->  synoptics.contains(stopRoute.synoptic)}

        val stops = sr
                .flatMap { stopRoute -> stopRoute.stops }
                .groupingBy { it }
                .eachCount()
                .filter { it.value == sr.size }
                .keys
                .sortedBy { stop -> stop.order }
                .toList()



        //Toast.makeText(requireContext(), stops.toString(), Toast.LENGTH_LONG).show()
        //Toast.makeText(requireContext(), route.lines.toString(), Toast.LENGTH_LONG).show()

        val adapter = StopRouteAdapter(stops)
        recycler_stops.adapter = adapter


        val manager = recycler_stops.layoutManager as LinearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(recycler_stops.context, manager.orientation)
        recycler_stops.addItemDecoration(dividerItemDecoration)


    }
}