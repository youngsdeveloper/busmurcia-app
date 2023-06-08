package com.youngsdeveloper.bus_murcia.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.gson.Gson
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.RouteClickListener
import com.youngsdeveloper.bus_murcia.adapters.StopAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PlaceFragment : Fragment(R.layout.fragment_place) {



    val args: PlaceFragmentArgs by navArgs()

    var global_stops : List<Stop>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun showError(){
        progressBar.visibility = View.GONE
        textError.visibility = View.VISIBLE
    }

    private fun downloadPlace(){

        requireActivity().title = args.place.name

        progressBar.indeterminateDrawable.setColorFilter(
                resources.getColor(R.color.tmp_murcia),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val place = args.place


            try {
                val call = ApiAdapter.getApiService().getStopsByCoordinates(place.lat, place.lon)

                if(call.isSuccessful){
                    val stops = call.body();


                    requireActivity().runOnUiThread {
                        stops?.let { stops -> loadPlace(stops) }

                    }
                }else{
                    showError()
                }
            }catch (e: Exception){
                requireActivity().runOnUiThread {
                    showError()
                }

            }


        }
    }


    private fun loadPlace(stops: List<Stop>){
        // Ok, cargar paradas en el RecyclerView
        //Toast.makeText(requireContext(), "Hola: $stops", Toast.LENGTH_LONG).show();


        if(isAdded){
            requireActivity().title = args.place.name
        }

        progressBar.visibility = View.GONE
        textError.visibility = View.GONE


        val routeClickListener = object : RouteClickListener {
            override fun onRouteClick(r: Route, s: Stop?) {


                s?.let { s ->

                    val action = PlaceFragmentDirections.actionPlaceFragmentToRouteFragment(r, r.id.toString(), s, s.name)
                    findNavController().navigate(action)
                }


            }
        }




        val adapter = StopAdapter(stops!!, routeClickListener)

        recycler_stops.addItemDecoration(DividerItemDecoration(context, 0))


        recycler_stops.adapter = adapter


        global_stops = stops
    }




    override fun onPause() {
        super.onPause()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)

        if (sharedPref != null) {
            with(sharedPref.edit()) {
                val json = Gson().toJson(global_stops)
                if(!json.equals("null") && !json.isNullOrEmpty()){
                    putString("place_" + args.place.name, json)
                }
                // Ahora solo se cachea las paradas durante un dia, porque pueden variar de dia a dia.
                putString("place_" + args.place.name + "_query_date", getCurrentDate())
                commit()
            }
        }
    }

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        return currentDate
    }


    override fun onResume() {
        super.onResume()


        var loaded_place = false

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = sharedPref?.getString("place_" + args.place.name, null)

        if(json != null && !json.equals("null")){

            var need_to_download = false;



            if(sharedPref?.contains("place_" + args.place.name + "_query_date")){
                val query_date = sharedPref?.getString("place_" + args.place.name + "_query_date", "")
                if(!query_date.equals(getCurrentDate()) ){
                    // Hay que descargar
                    need_to_download = true;

                }
            }else{
                need_to_download = true;
            }

            // Force download
            if(args.forceDownload){
                need_to_download = true;
            }

            if(!need_to_download){
                json?.let { json ->
                    val stops = Gson().fromJson(json, Array<Stop>::class.java).toList()
                    loadPlace(stops)
                    loaded_place = true

                }
            }

        }


        if(!loaded_place){
            downloadPlace()
        }

    }


}