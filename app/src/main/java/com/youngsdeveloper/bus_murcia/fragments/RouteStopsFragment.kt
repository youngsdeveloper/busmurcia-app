 package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.youngsdeveloper.bus_murcia.databinding.FragmentRouteStopsBinding
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop
import com.youngsdeveloper.bus_murcia.models.StopRoute
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


     private var _binding: FragmentRouteStopsBinding? = null
     private val binding get() = _binding!!


     override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View? {
         _binding = FragmentRouteStopsBinding.inflate(inflater, container, false)
         val view = binding.root
         return view
     }

     override fun onDestroyView() {
         super.onDestroyView()
         _binding = null
     }
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
            binding.textHeadsign.text = route.lines[0].headsign
        }else{
            binding.textHeadsign.text = ""
        }
        //

         if(route.getSynopticInRoute().isEmpty()){
             has_to_download_synoptic = true;
         }else{
             loadSynoptic(route)
         }

        downloadRouteStops(route);

        adapter = StopRouteAdapter(mutableListOf(), requireContext())
        binding.recyclerStops.adapter = adapter
        adapter.recyclerView = binding.recyclerStops

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
                    binding.chipGroupSynoptic,
                    false
            ) as Chip
            chip.text = "L${route.id} - ${synoptic}"
            chip.tag = synoptic
            chip.isChecked = synoptics.contains(synoptic)
            chip.setOnCheckedChangeListener(onCheckedListener)
            binding.chipGroupSynoptic.addView(chip)
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
                 binding.chipGroupSynoptic,
                 false
             ) as Chip
             chip.text = "L${route.id} - ${synoptic}"
             chip.tag = synoptic
             chip.isChecked = synoptics.contains(synoptic)
             chip.setOnCheckedChangeListener(onCheckedListener)
             binding.chipGroupSynoptic.addView(chip)
             chipList.add(chip)
         }

         Log.d("fromLines", args.fromLines.toString())
         if(args.fromLines && synps.size>1){
             binding.textHintSynoptic.visibility = View.VISIBLE
         }else{
             binding.textHintSynoptic.visibility = View.GONE
         }
     }

     private fun showError(){
         binding.recyclerStops.visibility = View.GONE
         binding.loadingStops.visibility = View.GONE
         binding.textError.visibility = View.VISIBLE

     }
    private fun downloadRouteStops(route: Route, load_active:Boolean=true){

        this.route = route

        binding.loadingStops.indeterminateDrawable.setColorFilter(
                resources.getColor(R.color.tmp_murcia),
                android.graphics.PorterDuff.Mode.SRC_IN);



        binding.loadingStops.visibility = View.VISIBLE
        binding.recyclerStops.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {


            try {
                val call = ApiAdapter
                    .getApiService()
                    .getLineStops(route.id)

                if(call.isSuccessful){
                    requireActivity().runOnUiThread {
                        val routes = call.body();
                        routes?.let { routes ->
                            loadRouteStop(routes, load_active)
                        }

                        if (routes != null) {
                            if(routes.isEmpty()){
                                binding.textEmpty.visibility = View.VISIBLE
                            }
                        }else{
                            binding.textEmpty.visibility = View.VISIBLE
                        }
                    }
                }else{
                    requireActivity().runOnUiThread {
                        showError()
                    }
                }
            }catch (e:Exception){
                requireActivity().runOnUiThread {
                    showError()
                }
            }





        }
    }

    private fun loadRouteStop(routes: List<StopRoute>, load_active: Boolean=true){


        binding.loadingStops.visibility = View.GONE
        binding.textError.visibility = View.GONE

        binding.recyclerStops.visibility = View.VISIBLE


        this.stopRoutes = routes

        if(has_to_download_synoptic){
            synoptics = stopRoutes.map { sr -> sr.synoptic }.distinct().toMutableList()
            loadSynoptic(synoptics)
            has_to_download_synoptic = false;
        }


        val sr = routes
                .filter { stopRoute ->  stopRoute.direction == route.getRealDirection()}
                .filter { stopRoute ->  synoptics.contains(stopRoute.synoptic)}

        Log.d("stops",sr
            .flatMap { stopRoute -> stopRoute.stops }.toString())
        val stops = sr
                .flatMap { stopRoute -> stopRoute.stops }
                .groupingBy { it }
                .eachCount()
                .filter { it.value == sr.size }
                .keys
                .sortedBy { stop -> stop.order }
                .toList()

        if(sr.isNotEmpty()){
            binding.textHeadsign.text = sr[0].headsign
            binding.textHeadsign.visibility = View.VISIBLE
        }else{
            binding.textHeadsign.visibility = View.GONE
        }



        adapter.items = stops
        adapter.notifyDataSetChanged()



        var loading_double = false;

        adapter.stopClickListener = object : StopClickListener {

            override fun onStopClick(stop: Stop, listener: StopRealtimeListener) {

                CoroutineScope(Dispatchers.IO).launch {




                    try {
                        val call_stop = ApiAdapter
                            .getApiService()
                            .getStopsByCoordinates(stop.latitude, stop.longitude)

                        if(call_stop.isSuccessful) {
                            val stops = call_stop.body();


                            val requested_stop = stops!!.filter { stp -> stp.id == stop.id }[0]


                            val requested_route =
                                requested_stop.getRoutes().filter { rt -> rt.id == route.id }[0]

                            val stopsList = listOf<String>(stop.id.toString())


                            /*
                            FIX: Direction
                            val linesList = args.route.lines
                                    .filter { line -> synoptics.contains(line.synoptic) }
                                    .filter { line -> line.direction == route.lines[0].direction }
                                    .map { line -> line.id }
                            */
                            var linesList = synoptics
                                .map { synoptic -> "${route.id}.${synoptic}.${route.getRealDirection()}" }


                            Log.d("lines_list", linesList.toString())

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
                    }catch (e:Exception){
                        requireActivity().runOnUiThread {
                            showError()

                        }
                    }


                }

            }


            override fun onStopDoubleClick(stop: Stop, _route: Route?) {


                // prevent crash double click

                if(loading_double){
                    return;
                }

                loading_double = true;

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






        val manager = binding.recyclerStops.layoutManager as LinearLayoutManager

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