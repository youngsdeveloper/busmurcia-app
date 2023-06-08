package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.AlertAdapter
import com.youngsdeveloper.bus_murcia.adapters.AlertClickListener
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.fetcher.request.UrlBuilder
import it.skrape.selects.eachHref
import it.skrape.selects.eachLink
import it.skrape.selects.eachText
import it.skrape.selects.html5.a
import it.skrape.selects.html5.table
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.android.synthetic.main.fragment_route.loading_realtime
import kotlinx.android.synthetic.main.fragment_ultima_hora.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.nio.charset.Charset
import kotlin.concurrent.thread

class UltimaHoraFragment : Fragment(R.layout.fragment_ultima_hora) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loading_realtime.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.tmp_murcia),
            android.graphics.PorterDuff.Mode.SRC_IN);

        fetchData()
    }

    private fun fetchData(){


        thread {

            try {
                val source = URL("http://tmpmurcia.es/ultima.asp").readText(Charset.forName("ISO-8859-1"))
                val avisosMap = parseHTML(source)
                requireActivity().runOnUiThread {
                    setupUI(avisosMap)
                }
            }catch (e:Exception){
                requireActivity().runOnUiThread {
                    loading_realtime.visibility = View.GONE
                    textError.visibility = View.VISIBLE

                }
            }


        }
    }

    private fun setupUI(avisosMap:Map<String,String> ){

        loading_realtime.visibility = View.GONE

        val alertAdapter = AlertAdapter(avisosMap)

        alertAdapter.listener = object:AlertClickListener{
            override fun onAlertClicked(codigo: String) {
                val args = bundleOf("codigo" to codigo)
                findNavController().navigate(R.id.action_ultimaHoraFragment_to_alertFragment, args)
            }

        }

        recycler_alertas.adapter = alertAdapter



        println(avisosMap)

    }

    private fun parseHTML(html:String):Map<String,String>{
        var avisosMap = htmlDocument(html) {
            table{
                findFirst {
                    tr {
                        a{
                            findAll{
                                eachLink
                            }
                        }
                    }
                }
            }
        }

        avisosMap = avisosMap.mapValues { v -> v.value.removePrefix("Cuerpo.asp?codigo=") }


        return avisosMap
    }
}