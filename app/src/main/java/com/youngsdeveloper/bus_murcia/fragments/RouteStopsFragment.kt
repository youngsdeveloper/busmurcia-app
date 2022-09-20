 package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.StopClickListener
import com.youngsdeveloper.bus_murcia.adapters.StopRealtimeListener
import com.youngsdeveloper.bus_murcia.adapters.StopRouteAdapter
import com.youngsdeveloper.bus_murcia.adapters.TMPAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop
import com.youngsdeveloper.bus_murcia.models.StopRoute
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

    lateinit var adapter:StopRouteAdapter

    private var synoptics = mutableListOf<String>();


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setHasOptionsMenu(true)
     }


     override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
         super.onCreateOptionsMenu(menu, inflater)
         inflater?.inflate(R.menu.menu_route_stops, menu)
     }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {


         when(item.itemId){
             R.id.action_change_direction -> change_direction()
         }

         return super.onOptionsItemSelected(item)
     }

     private var has_to_download_synoptic = false;

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val route = args.route
        this.route = route

        this.synoptics = args.synoptics.toMutableList()

        if(route.lines.isNotEmpty()){
            text_headsign.text = route.lines[0].headsign
        }else{
            text_headsign.text = ""
        }
        //

         if(route.getSynopticInRoute().isEmpty()){
             has_to_download_synoptic = true;
         }else{
             loadSynoptic(route)
         }

        downloadRouteStops(route);

        adapter = StopRouteAdapter(mutableListOf(), requireContext())
        recycler_stops.adapter = adapter
        adapter.recyclerView = recycler_stops

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

     private fun loadSynoptic(synps: List<String>){
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

         for(synoptic in synps){
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

         Log.d("fromLines", args.fromLines.toString())
         if(args.fromLines && synps.size>1){
             text_hint_synoptic.visibility = View.VISIBLE
         }else{
             text_hint_synoptic.visibility = View.GONE
         }
     }

    private fun downloadRouteStops(route: Route, load_active:Boolean=true){

        this.route = route

        loading_stops.indeterminateDrawable.setColorFilter(
                resources.getColor(R.color.tmp_murcia),
                android.graphics.PorterDuff.Mode.SRC_IN);



        loading_stops.visibility = View.VISIBLE
        recycler_stops.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {

            val call = ApiAdapter
                    .getApiService()
                    .getLineStops(route.id)



            requireActivity().runOnUiThread {

                if(call.isSuccessful){
                    val routes = call.body();
                    routes?.let { routes ->
                        loadRouteStop(routes, load_active)
                    }

                    if (routes != null) {
                        if(routes.isEmpty()){
                            text_empty.visibility = View.VISIBLE
                        }
                    }else{
                        text_empty.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun loadRouteStop(routes: List<StopRoute>, load_active: Boolean=true){


        loading_stops.visibility = View.GONE
        recycler_stops.visibility = View.VISIBLE

        this.stopRoutes = routes

        if(has_to_download_synoptic){
            synoptics = stopRoutes.map { sr -> sr.synoptic }.distinct().toMutableList()
            loadSynoptic(synoptics)
            has_to_download_synoptic = false;
        }


        val sr = routes
                .filter { stopRoute ->  stopRoute.direction == route.getRealDirection()}
                .filter { stopRoute ->  synoptics.contains(stopRoute.synoptic)}

        val stops = sr
                .flatMap { stopRoute -> stopRoute.stops }
                .groupingBy { it }
                .eachCount()
                .filter { it.value == sr.size }
                .keys
                .sortedBy { stop -> stop.order }
                .toList()

        if(sr.isNotEmpty()){
            text_headsign.text = sr[0].headsign
            text_headsign.visibility = View.VISIBLE
        }else{
            text_headsign.visibility = View.GONE
        }



        adapter.items = stops
        adapter.notifyDataSetChanged()




        adapter.stopClickListener = object : StopClickListener {

            override fun onStopClick(stop: Stop, listener: StopRealtimeListener) {

                CoroutineScope(Dispatchers.IO).launch {



                    val call_stop = ApiAdapter
                            .getApiService()
                            .getStopsByCoordinates(stop.latitude, stop.longitude)

                    if(call_stop.isSuccessful){
                        val stops = call_stop.body();


                        val requested_stop = stops!!.filter { stp -> stp.id == stop.id }[0]


                        val requested_route = requested_stop.getRoutes().filter{ rt -> rt.id == route.id }[0]

                        val stopsList = listOf<String>(stop.id.toString())
                        val linesList = args.route.lines
                                .filter { line -> synoptics.contains(line.synoptic) }
                                .filter { line -> line.direction == route.lines[0].direction }
                                .map { line -> line.id }


                        val call = ApiAdapter
                                .getApiService()
                                .getRealTimeHours(stopsList, linesList)



                        if(call.isSuccessful){
                            val realtime_hours = call.body();



                            var tmpAdapter = TMPAdapter(requested_route)
                            tmpAdapter.activeSynoptics = synoptics
                            tmpAdapter.realtime_hours = realtime_hours!!


                            if(isAdded){
                                requireActivity().runOnUiThread {



                                    var status_min = tmpAdapter.getRealTimeData().status_min!!
                                    if(status_min==null){
                                        status_min = "No info"
                                    }

                                    listener.onStopRealtimeLoaded(stop, status_min, requested_route)
                                }
                            }


                        }

                    }


                }

            }


            override fun onStopDoubleClick(stop: Stop, _route: Route?) {

                var rt:Route

                if(_route!=null){
                    rt = _route!!

                    val action = RouteStopsFragmentDirections.actionRouteStopsFragmentToRouteFragment(rt, rt.id.toString(), stop, stop.name)
                    findNavController().navigate(action)

                }else{


                    CoroutineScope(Dispatchers.IO).launch {



                        val call_stop = ApiAdapter
                                .getApiService()
                                .getStopsByCoordinates(stop.latitude, stop.longitude)

                        if(call_stop.isSuccessful){
                            val stops = call_stop.body();



                            val requested_stop = stops!!.filter { stp -> stp.id == stop.id }[0]


                            val requested_route = requested_stop.getRoutes().filter{ rt -> rt.id == route.id }[0]

                            requireActivity().runOnUiThread {
                                val action = RouteStopsFragmentDirections.actionRouteStopsFragmentToRouteFragment(requested_route, requested_route.id.toString(), stop, stop.name)
                                findNavController().navigate(action)

                            }

                        }


                    }


                }

            }
        }

        if(stops.isNotEmpty() && load_active){

            args.stop?.let { stop ->
                adapter.loadActive(stop.name)
            }

        }






        val manager = recycler_stops.layoutManager as LinearLayoutManager

        //val dividerItemDecoration = DividerItemDecoration(recycler_stops.context, manager.orientation)
        //recycler_stops.addItemDecoration(dividerItemDecoration)


    }

     private fun change_direction(){
         val old_active = adapter.active_item

         if(!route.lines.isEmpty()){
             if(route.lines[0].direction==1){
                 route.lines[0].direction = 2;
                 route.direction = 2;
             }else{
                 route.lines[0].direction = 1;
                 route.direction = 1;
             }
         }else{
             if(route.direction==1){
                 route.direction=2;
             }else{
                 route.direction=1;
             }
         }

         downloadRouteStops(route, true)
         // FIX: Load active by name
         // Cant search same stop for other direction


         /*
         if(old_active!=null){
             if(adapter.items.isNotEmpty()){
                 var stop_other_dir = adapter.items.filter { stp -> stp.name.contains(old_active.name) }

                 if(stop_other_dir.size>0){
                     adapter.loadActive(stop_other_dir[0])
                 }
                 //Toast.makeText(requireContext(), stop_other_dir.toString(), Toast.LENGTH_LONG).show()
             }
         }
         */
     }
}