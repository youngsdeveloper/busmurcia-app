package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.AlternativeTMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.IArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.PanelTMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.StopArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.TMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.PanelItem
import com.youngsdeveloper.bus_murcia.models.RealTimeHour
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

    private fun loadAlternativeStop(hours: List<RealTimeHour>){

        loading.visibility = View.GONE
        recycler_llegadas.visibility = View.VISIBLE


        val IArrivalsAdapter = AlternativeTMPArrivalsAdapter(hours)


        val arrivals = IArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = arrivals

        recycler_llegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            text_empty.visibility = View.GONE
        }else{
            text_empty.visibility = View.VISIBLE
        }
    }

    private fun loadPanel(items: List<PanelItem>){

        loading.visibility = View.GONE
        recycler_llegadas.visibility = View.VISIBLE


        val panelTMPArrivalsAdapter = PanelTMPArrivalsAdapter(items)


        val arrivals = panelTMPArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = stop_arrrivals_adapter.items.plus(arrivals)

        stop_arrrivals_adapter.items = stop_arrrivals_adapter.items.sortedBy{it.minutes}
        recycler_llegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            text_empty.visibility = View.GONE
        }else{
            text_empty.visibility = View.VISIBLE
        }
    }


    private suspend fun downloadPanel(){

        val call = ApiAdapter.getApiService().getPanel(args.stop.id.toInt());

        val panel = call.body()!!;


        requireActivity().runOnUiThread {
            loadPanel(panel)
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

                downloadPanel()


            }catch (e:Exception){
                Log.e("error", e.localizedMessage)
                requireActivity().runOnUiThread {
                    loading.visibility = View.GONE
                    text_error.visibility = View.VISIBLE
                }
            }


        }

    }
}