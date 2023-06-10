package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.IArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.StopArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.TMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlinx.android.synthetic.main.fragment_place.*
import kotlinx.android.synthetic.main.fragment_stop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StopFragment : Fragment(R.layout.fragment_stop) {

    val args: StopFragmentArgs by navArgs()

    val stop_arrrivals_adapter = StopArrivalsAdapter(mutableListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_arrivals_stop, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_refresh -> downloadStop()
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        text_parada.text = args.stopName

        downloadStop()
    }

    private fun loadStop(s: Stop){

        loading.visibility = View.GONE
        recycler_llegadas.visibility = View.VISIBLE


        val IArrivalsAdapter = TMPArrivalsAdapter(s)


        val arrivals = IArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = arrivals

        recycler_llegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            text_empty.visibility = View.GONE
        }else{
            text_empty.visibility = View.VISIBLE
        }


    }
    private fun downloadStop(){

        recycler_llegadas.visibility = View.GONE
        text_empty.visibility = View.GONE
        text_error.visibility = View.GONE

        loading.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.tmp_murcia),
            android.graphics.PorterDuff.Mode.SRC_IN);

        loading.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val call = ApiAdapter.getApiService().getStop(args.stop.id.toInt())

                if(call.isSuccessful){
                    val stop = call.body()

                    requireActivity().runOnUiThread {

                        stop?.let { stop -> loadStop(stop)}
                    }

                }else{
                    requireActivity().runOnUiThread {
                        text_empty.visibility = View.GONE
                    }
                }

            }catch (e:Exception){
                requireActivity().runOnUiThread {
                    loading.visibility = View.GONE
                    text_error.visibility = View.VISIBLE
                }
            }


        }

    }
}