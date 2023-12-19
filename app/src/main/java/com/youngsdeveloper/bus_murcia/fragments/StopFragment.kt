package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.AlternativeTMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.IArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.StopArrivalsAdapter
import com.youngsdeveloper.bus_murcia.adapters.TMPArrivalsAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
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


    private suspend fun downloadAlternativeStop(){

        val call = ApiAdapter.getApiService().getRealTimeHours(listOf(args.stop.id.toString()), listOf());

        val hours = call.body()!!;


        requireActivity().runOnUiThread {
            loadAlternativeStop(hours)
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

                    // FIX: Linea 44
                    stop?.let { stop ->

                        if(stop.lines.isEmpty()){
                            // Probar descarga de 2º tipo
                            downloadAlternativeStop();
                            return@launch;
                        }

                        if(stop.lines.any { line -> line.route==44 }){
                            // Contiene alguna linea 44...
                            // Hay que hacer otra peticion para mapear

                            val call2 = ApiAdapter.getApiService().getRealTimeHours(
                                mutableListOf(args.stop.id.toString()),
                                mutableListOf()
                            )

                            if(call2.isSuccessful){

                                val realtime_list = call2.body()

                                realtime_list?.let { realtime_list ->
                                    realtime_list.filter { rt -> rt.line_id.toInt()==44 }.forEach { rt ->
                                        var line  = stop.lines.filter { l -> l.id.startsWith("${rt.line_id}.${rt.synoptic}") }.firstOrNull()
                                        line?.let { line->
                                            if(line.realtime==null){
                                                line.realtime = mutableListOf(rt)
                                            }else{
                                                line.realtime!!.add(rt)
                                            }
                                        }
                                    }
                                }



                            }


                        }

                        // FIX 39

                        if(stop.lines.any { line -> line.route==39 }){
                            // Contiene alguna linea 39...
                            // Hay que hacer otra peticion para mapear

                            val call2 = ApiAdapter.getApiService().getRealTimeHours(
                                mutableListOf(args.stop.id.toString()),
                                mutableListOf()
                            )

                            if(call2.isSuccessful){

                                val realtime_list = call2.body()

                                realtime_list?.let { realtime_list ->
                                    realtime_list.filter { rt -> rt.line_id.toInt()==39 }.forEach { rt ->
                                        var line  = stop.lines.filter { l -> l.id.startsWith("${rt.line_id}.${rt.synoptic}") }.firstOrNull()
                                        line?.let { line->
                                            if(line.realtime==null){
                                                line.realtime = mutableListOf(rt)
                                            }else{
                                                line.realtime!!.add(rt)
                                            }
                                        }
                                    }
                                }



                            }


                        }
                    }



                    requireActivity().runOnUiThread {
                        Log.d("stop_lines", stop!!.lines.toString())


                        stop?.let { stop -> loadStop(stop)}
                    }

                }else{
                    requireActivity().runOnUiThread {
                        text_empty.visibility = View.GONE
                    }
                }

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