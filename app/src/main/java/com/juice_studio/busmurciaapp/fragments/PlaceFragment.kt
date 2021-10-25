package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.RouteClickListener
import com.juice_studio.busmurciaapp.adapters.StopAdapter
import com.juice_studio.busmurciaapp.io.ApiAdapter
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceFragment : Fragment(R.layout.fragment_place) {



    val args: PlaceFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fetchPlace()

    }


    private fun fetchPlace(){

        requireActivity().title = args.place.name

        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val place = args.place
            val call = ApiAdapter.getApiService().getStopsByCoordinates(place.lat, place.lon)
            val stops = call.body();


            requireActivity().runOnUiThread {
                if(call.isSuccessful){
                    // Ok, cargar paradas en el recyclervire
                    //Toast.makeText(requireContext(), "Hola: $stops", Toast.LENGTH_LONG).show();

                    progressBar.visibility = View.GONE


                    val routeClickListener = object : RouteClickListener {
                        override fun onRouteClick(r: Route,s: Stop) {

                            val action = PlaceFragmentDirections.actionPlaceFragmentToRouteFragment(r,r.id.toString(),  s)
                            findNavController().navigate(action)

                        }
                    }

                    val adapter = StopAdapter(stops!!, routeClickListener)
                    recycler_stops.adapter = adapter
                }else{
                    Toast.makeText(requireContext(), "Fallo!", Toast.LENGTH_LONG).show();

                }
            }
        }
    }
}