package com.youngsdeveloper.bus_murcia.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.*
import android.widget.CompoundButton
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.chip.Chip
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.HourAdapter
import com.youngsdeveloper.bus_murcia.adapters.ITMPAdapter
import com.youngsdeveloper.bus_murcia.adapters.TMPAdapter
import com.youngsdeveloper.bus_murcia.io.ApiAdapter
import com.youngsdeveloper.bus_murcia.local.AppDatabase
import com.youngsdeveloper.bus_murcia.models.Place
import com.youngsdeveloper.bus_murcia.models.RealTimeHour
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.toPlaceEntity
import kotlinx.android.synthetic.main.fragment_new_place.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


private const val SECONDS = 1000


class RouteFragment : Fragment(R.layout.fragment_route) {

    val args: RouteFragmentArgs by navArgs()

    private lateinit var hours_adapter :HourAdapter
    private val chipList = mutableListOf<Chip>()


    lateinit var mainHandler: Handler


    lateinit var tmpAdapter:ITMPAdapter

    var synoptics = mutableListOf<String>()


    private val updateRealtimeTask = object : Runnable {
        override fun run() {
            downloadLocalRealTimeData()
            mainHandler.postDelayed(this, (5 * SECONDS).toLong())
        }
    }


    private val updateRealtimeRemoteTask = object : Runnable {
        override fun run() {
            downloadRemotelRealTimeData()
            mainHandler.postDelayed(this, (30 * SECONDS).toLong())
        }
    }


    private lateinit var realtime_hours: List<RealTimeHour>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handler
        mainHandler = Handler(Looper.getMainLooper())

        setHasOptionsMenu(true)

        val route = args.route
        this.synoptics = route.getSynopticInRoute().toMutableList()


    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_route, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when(item.itemId){
            R.id.action_favorito -> saveStopAsFavouritePlace()
            R.id.action_reload_route -> downloadRemotelRealTimeData()
            R.id.action_see_route -> openRouteStops()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openRouteStops(){
        val action = RouteFragmentDirections.actionRouteFragmentToRouteStopsFragment(args.route, args.routeTitle, args.stop, args.stopName, getActiveSynoptics().toTypedArray(), false)
        findNavController().navigate(action)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val route = args.route


        //Creamos el adaptador
        tmpAdapter = TMPAdapter(route)

        //Crea el adaptador de horas
        hours_adapter = HourAdapter(mutableListOf())
        recycler_hours.adapter = hours_adapter


        // Initializa real time hours
        this.realtime_hours = args.route.getRealTimeHours()



        loadRoute(route)

        downloadLocalRealTimeData()
        downloadRemotelRealTimeData()
    }



    private fun loadRoute(route: Route){

        text_route_number.text = "L${route.id}"
        text_route_headsign.text = "${route.getRouteHeadsign()}"

        loadSynoptic(route)
        updateHours()

        GridLayoutManager(
                requireContext(),
                4,
                RecyclerView.VERTICAL,
                false
        ).apply {
            recycler_hours.layoutManager = this
        }

        switch_full_hours.setOnCheckedChangeListener { buttonView, isChecked ->
            updateHours()
        }

    }

    private fun loadSynoptic(route: Route){
        val onCheckedListener = object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {



                val synoptic = buttonView?.tag as String

                if(isChecked){
                    synoptics.add(synoptic)
                }else{
                    synoptics.remove(synoptic)
                }

                updateHours();
                downloadLocalRealTimeData()

                /*
                buttonView?.let { button ->
                    synoptics.add(button.getTag(4).toString())
                }*/

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



    private fun downloadRemotelRealTimeData(){

        if(!isAdded){
            return; // Fix crash bug
        }


        text_next_bus.visibility = View.GONE
        text_status_next_bus.visibility = View.GONE
        text_next_bus_line.visibility = View.GONE

        loading_realtime.visibility = View.VISIBLE;
        loading_realtime.indeterminateDrawable.setColorFilter(
                resources.getColor(R.color.tmp_murcia),
                android.graphics.PorterDuff.Mode.SRC_IN);

        CoroutineScope(Dispatchers.IO).launch {


            val stopsList = listOf<String>(args.stop.id.toString())
            val linesList = args.route.lines.map { line -> line.id }

            val call = ApiAdapter
                    .getApiService()
                    .getRealTimeHours(stopsList, linesList)



            if(isAdded){
                requireActivity().runOnUiThread {

                    if(call.isSuccessful){
                        val realtime_hours = call.body();
                        realtime_hours?.let { realtime_hours ->
                            loadRealTimeData(realtime_hours)
                        }
                    }
                }
            }


        }
    }

    private fun downloadLocalRealTimeData(){
        loadRealTimeData(realtime_hours)
    }

    private fun loadRealTimeData(realtime_hours: List<RealTimeHour>){

        if(loading_realtime==null){
            return;
        }


        loading_realtime.visibility = View.GONE;

        this.realtime_hours = realtime_hours



        tmpAdapter.realtime_hours = realtime_hours
        val realTimeData = tmpAdapter.getRealTimeData()

        text_next_bus_line.text = realTimeData.linea_cabecera
        text_status_next_bus.text = realTimeData.status
        text_next_bus.text = realTimeData.llegada_estimada

        text_status_next_bus.setTextColor(ContextCompat.getColor(requireContext(), realTimeData.status_color))

        text_status_next_bus.visibility = View.VISIBLE

        if(realTimeData.linea_cabecera.isEmpty()){
            text_next_bus_line.visibility = View.GONE
        }else{
            text_next_bus_line.visibility = View.VISIBLE
        }

        if(realTimeData.llegada_estimada.isEmpty()){
            text_next_bus.visibility = View.GONE
        }else{
            text_next_bus.visibility = View.VISIBLE
        }
    }

    private fun updateHours(){

        tmpAdapter.activeSynoptics = getActiveSynoptics()

        var hours = tmpAdapter.getNextHours()

        if(switch_full_hours.isChecked){
            hours = tmpAdapter.getFullHours()
        }

        hours_adapter.items = hours
        hours_adapter.notifyDataSetChanged()
    }

    private fun getActiveSynoptics():List<String>{

        /*
        val synoptics = mutableListOf<String>()

        for(chip in chipList){

            if(chip.isChecked){

                val synoptic = chip.tag as String
                synoptics.add(synoptic)
            }
        }

        return Collections.unmodifiableList(synoptics)*/
        return synoptics;
    }


    private fun storeFavStop(place_name:String){
        CoroutineScope(Dispatchers.IO).launch {

            val appDatabase = AppDatabase
                .getDatabase(requireContext())


            val stop = args.stop;

            val place = Place(-1, place_name, stop.latitude, stop.longitude)
            appDatabase.placeDao()
                .savePlace(place.toPlaceEntity())

            requireActivity().runOnUiThread {
                showSuccesPlaceSaved()
            }
        }
    }


    private fun showSuccesPlaceSaved(){
        MaterialDialog(requireContext()).show {
            icon(R.drawable.ic_star_red)
            title(text = "Sitio favorito guardado.")
            message(text = "¡Listo!\n\nYa has agregado este sitio a tu lista de sitios favoritos.\n\nPuedes verlo en la pantalla principal.")
            positiveButton( text = "De acuerdo")
        }
    }
    private fun saveStopAsFavouritePlace(){


        MaterialDialog(requireContext()).show {
            icon(R.drawable.ic_star_red)
            title(text = "Guardar parada en sitios favoritos.")
            message(text = "Estas a punto de guardar la ubicación de esta parada en tus sitios favoritos.\n\nPuedes ponerle el nombre que desees.")

            input { dialog, text ->
                // Text submitted with the action button
                storeFavStop(text.toString())
            }
            negativeButton(text = "Cancelar")
            positiveButton( R.string.dialog_save_fav_positive_button)
        }
    }


    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateRealtimeTask) //
        mainHandler.removeCallbacks(updateRealtimeRemoteTask) //

    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateRealtimeTask) //Update Real Time
        mainHandler.post(updateRealtimeRemoteTask) //Update Real Time Remote

    }
}