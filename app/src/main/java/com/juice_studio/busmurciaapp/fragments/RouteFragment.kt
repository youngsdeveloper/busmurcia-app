package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.HourAdapter
import com.juice_studio.busmurciaapp.io.ApiAdapter
import com.juice_studio.busmurciaapp.models.Hour
import com.juice_studio.busmurciaapp.models.RealTimeHour
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val SECONDS = 1000


class RouteFragment : Fragment(R.layout.fragment_route) {

    val args: RouteFragmentArgs by navArgs()

    private lateinit var hours_adapter :HourAdapter
    private val chipList = mutableListOf<Chip>()


    lateinit var mainHandler: Handler


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

        if(item.itemId==R.id.action_reload_route){
            downloadRemotelRealTimeData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val route = args.route
        text_route_number.text = "L${route.id}"
        text_route_headsign.text = "${route.getRouteHeadsign()}"

        val onCheckedListener = object:CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                hours_adapter.items = getHoursFiltered()
                hours_adapter.notifyDataSetChanged()
                downloadLocalRealTimeData()
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
            chip.setOnCheckedChangeListener(onCheckedListener)
            chip_group_synoptic.addView(chip)
            chipList.add(chip)
        }

        // Initializa real time hours
        this.realtime_hours = args.route.getRealTimeHours()

        downloadLocalRealTimeData()


        var hours = route.getLineHours()
        if(!switch_full_hours.isChecked){
            hours = hours.filter { hour -> hour.date.after(Date()) }
        }

        hours_adapter = HourAdapter(hours)
        recycler_hours.adapter = hours_adapter

        GridLayoutManager(
            requireContext(),
            4,
            RecyclerView.VERTICAL,
            false
        ).apply {
            recycler_hours.layoutManager = this
        }

        switch_full_hours.setOnCheckedChangeListener { buttonView, isChecked ->
            hours_adapter.items = getHoursFiltered()
            hours_adapter.notifyDataSetChanged()
        }


        downloadRemotelRealTimeData()



    }


    private fun downloadRemotelRealTimeData(){

        CoroutineScope(Dispatchers.IO).launch {


            val stopsList = listOf<String>(args.stop.id.toString())
            val linesList = args.route.lines.map { line -> line.id }

            val call = ApiAdapter
                    .getApiService()
                    .getRealTimeHours(stopsList, linesList)



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


    private fun downloadLocalRealTimeData(){
        loadRealTimeData(realtime_hours)
    }

    private fun loadRealTimeData(realtime_hours: List<RealTimeHour>){

        this.realtime_hours = realtime_hours

        text_next_bus.text = "No hay información en tiempo real"
        text_status_next_bus.visibility = View.GONE
        text_next_bus_line.visibility = View.GONE
        text_next_bus.visibility = View.VISIBLE

        var realtime_hours = realtime_hours.filter { rt -> getActiveSynoptics().contains(rt.synoptic) }



        var min_nearest_hour: Hour? = null
        var min_realtime: RealTimeHour? = null

        for(realtime in realtime_hours){

            var base_time = Date()
            if(realtime!!.real_time.matches(".*\\d.*".toRegex())){
                val calendar = Calendar.getInstance()
                calendar.time = base_time
                val minutes = realtime.real_time.filter { it.isDigit() }
                calendar.add(Calendar.MINUTE, minutes.toInt())
                base_time = calendar.time

            }


            val nearest_hour = args.route.findNearestHour(realtime.synoptic, base_time)

            if(min_nearest_hour==null){
                min_nearest_hour = nearest_hour
                min_realtime = realtime
            }else if(nearest_hour!!.date.before(min_nearest_hour.date)){
                min_nearest_hour = nearest_hour
                min_realtime = realtime
            }

        }


        min_nearest_hour?.let { nearest_hour ->

            text_status_next_bus.visibility = View.VISIBLE
            text_next_bus_line.visibility = View.VISIBLE

            text_next_bus_line.text = "L" + min_realtime!!.line_id + "-" + min_realtime!!.synoptic

            var estimated_hour = nearest_hour.date
            var cal = Calendar.getInstance()
            cal.time = estimated_hour


            if(min_realtime!!.isAdelantado()){
                cal.add(Calendar.MINUTE, min_realtime!!.delay_minutes.toInt() * (-1))
            }else if(min_realtime!!.isRetrasado()){
                cal.add(Calendar.MINUTE, min_realtime!!.delay_minutes.toInt())
            }

            cal.set(Calendar.SECOND, 0) // Set seconds to 0

            estimated_hour = cal.time
            val format: DateFormat = SimpleDateFormat("HH:mm")


            val diff = estimated_hour.time - Date().time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff) +
                    1
            text_next_bus.text = "Llegada estimada: " + format.format(estimated_hour)
            //text_next_bus.text = nearest_hour?.hour_string + " " + nearest_hour.synoptic + "\n" + realtime.delay_string.capitalize() + " " +realtime.delay_minutes +  " minutos" + "\n" + realtime.real_time


            if(diffInMinutes>=1){
                text_next_bus.text = text_next_bus.text.toString() + " (En " + diffInMinutes + pluralize(
                    diffInMinutes.toInt(),
                    " minuto"
                ) + ")"
            }

            //

            if(min_realtime!!.real_time.matches(".*\\d.*".toRegex())){
                if(min_realtime!!.isEnHora()){
                    text_status_next_bus.text = "En hora"
                    text_status_next_bus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green_success
                        )
                    )
                }else{
                    text_status_next_bus.text = min_realtime!!.delay_string.capitalize() + " " + min_realtime!!.delay_minutes + " " + pluralize(
                        min_realtime!!.delay_minutes.toInt(),
                        "minuto"
                    )
                    text_status_next_bus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_danger
                        )
                    )
                }
                text_next_bus.visibility = View.VISIBLE

            }else{
                text_status_next_bus.text = min_realtime!!.real_time
                text_status_next_bus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.green_success
                    )
                )
                text_next_bus.visibility = View.GONE
            }

        }
    }

    private fun pluralize(cant: Int, str: String):String{
        if(cant>1){
            return str + "s"
        }
        return str
    }

    private fun getHoursFiltered():List<Hour>{

        var hours = args.route.getLineHours()
        hours = hours.filter { hour -> getActiveSynoptics().contains(hour.synoptic) }

        if(!switch_full_hours.isChecked){
            var current = Calendar.getInstance()
            current.add(Calendar.MINUTE, -30)
            hours = hours.filter { hour -> hour.date.after(current.time) }
        }

        return hours
    }

    private fun getActiveSynoptics():List<String>{

        val synoptics = mutableListOf<String>()

        for(chip in chipList){

            if(chip.isChecked){

                val synoptic = chip.tag as String
                synoptics.add(synoptic)
            }
        }

        return Collections.unmodifiableList(synoptics)
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