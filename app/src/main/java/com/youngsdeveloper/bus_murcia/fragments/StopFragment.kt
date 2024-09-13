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
import com.youngsdeveloper.bus_murcia.databinding.FragmentStopBinding
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.models.PanelItem
import com.youngsdeveloper.bus_murcia.models.RealTimeHour
import com.youngsdeveloper.bus_murcia.models.Stop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StopFragment : Fragment(R.layout.fragment_stop) {

    val args: StopFragmentArgs by navArgs()

    val stop_arrrivals_adapter = StopArrivalsAdapter(mutableListOf())

    private var _binding: FragmentStopBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStopBinding.inflate(inflater, container, false)
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
        inflater?.inflate(R.menu.menu_arrivals_stop, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_refresh -> downloadStop()
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.textParada.text = args.stopName

        downloadStop()
    }

    private fun loadStop(s: Stop){

        binding.loading.visibility = View.GONE
        binding.recyclerLlegadas.visibility = View.VISIBLE


        val IArrivalsAdapter = TMPArrivalsAdapter(s)


        val arrivals = IArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = arrivals

        binding.recyclerLlegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            binding.textEmpty.visibility = View.GONE
        }else{
            binding.textEmpty.visibility = View.VISIBLE
        }


    }

    private fun loadAlternativeStop(hours: List<RealTimeHour>){

        binding.loading.visibility = View.GONE
        binding.recyclerLlegadas.visibility = View.VISIBLE


        val IArrivalsAdapter = AlternativeTMPArrivalsAdapter(hours)


        val arrivals = IArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = arrivals

        binding.recyclerLlegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            binding.textEmpty.visibility = View.GONE
        }else{
            binding.textEmpty.visibility = View.VISIBLE
        }
    }

    private fun loadPanel(items: List<PanelItem>){

        binding.loading.visibility = View.GONE
        binding.recyclerLlegadas.visibility = View.VISIBLE


        val panelTMPArrivalsAdapter = PanelTMPArrivalsAdapter(items)


        val arrivals = panelTMPArrivalsAdapter.getArrivalsRealTime()

        stop_arrrivals_adapter.items = arrivals

        stop_arrrivals_adapter.items = stop_arrrivals_adapter.items.sortedBy{it.minutes}
        binding.recyclerLlegadas.adapter = stop_arrrivals_adapter

        if(arrivals.size>0){
            binding.textEmpty.visibility = View.GONE
        }else{
            binding.textEmpty.visibility = View.VISIBLE
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

        binding.recyclerLlegadas.visibility = View.GONE
        binding.textError.visibility = View.GONE
        binding.textError.visibility = View.GONE

        binding.loading.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.tmp_murcia),
            android.graphics.PorterDuff.Mode.SRC_IN);

        binding.loading.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {

            try {

                downloadPanel()


            }catch (e:Exception){
                Log.e("error", e.localizedMessage)
                requireActivity().runOnUiThread {
                    binding.loading.visibility = View.GONE
                    binding.textError.visibility = View.VISIBLE
                }
            }


        }

    }
}