package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.AlertAdapter
import com.youngsdeveloper.bus_murcia.adapters.AlertClickListener
import com.youngsdeveloper.bus_murcia.databinding.FragmentUltimaHoraBinding
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.nio.charset.Charset
import kotlin.concurrent.thread

class UltimaHoraFragment : Fragment(R.layout.fragment_ultima_hora) {


    private var _binding: FragmentUltimaHoraBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUltimaHoraBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.loadingRealtime.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.tmp_murcia),
            android.graphics.PorterDuff.Mode.SRC_IN);

        fetchData()
    }

    private fun fetchData(){


        thread {

            try {
                val source = URL("https://tmpmurcia.es/ultima.asp").readText(Charset.forName("ISO-8859-1"))
                Log.d("src",source)
                val avisosMap = parseHTML(source)
                requireActivity().runOnUiThread {
                    setupUI(avisosMap)
                }
            }catch (e:Exception){
                Log.e("ultimaHora",e.message.toString())
                requireActivity().runOnUiThread {
                    binding.loadingRealtime.visibility = View.GONE
                    binding.textError.visibility = View.VISIBLE

                }
            }


        }
    }

    private fun setupUI(avisosMap:Map<String,String> ){

        binding.loadingRealtime.visibility = View.GONE

        val alertAdapter = AlertAdapter(avisosMap)

        alertAdapter.listener = object:AlertClickListener{
            override fun onAlertClicked(codigo: String) {
                val args = bundleOf("codigo" to codigo)
                findNavController().navigate(R.id.action_ultimaHoraFragment_to_alertFragment, args)
            }

        }

        binding.recyclerAlertas.adapter = alertAdapter



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