package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.adapters.RouteAdapter
import com.youngsdeveloper.bus_murcia.adapters.RouteClickListener
import com.youngsdeveloper.bus_murcia.databinding.FragmentLineasBinding
import com.youngsdeveloper.bus_murcia.models.Route
import com.youngsdeveloper.bus_murcia.models.Stop

class LineasFragment : Fragment(R.layout.fragment_lineas) {

    private var _binding: FragmentLineasBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLineasBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openRouteStops(r: Route){



        val action = LineasFragmentDirections.actionLineasFragmentToRouteStopsFragment(r, r.id.toString(), null, null, arrayOf(), true)
        findNavController().navigate(action)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val routeClickListener = object : RouteClickListener {
            override fun onRouteClick(r: Route, s: Stop?) {

                openRouteStops(r)


            }
        }

        val routes = mutableListOf<Route>()

        routes.add(Route(1, listOf(), "San Ginés — Murcia"))
        routes.add(Route(6, listOf(), "La Alberca — Murcia"))
        routes.add(Route(7, listOf(), "La Albatalia — Murcia — La Arboleja"))
        routes.add(Route(11, listOf(), "Alcantarilla - UCAM - Campus Espinardo"))
        routes.add(Route(13, listOf(), "Alcantarilla - Poligono Industrial Oeste - Arrixaca"))
        routes.add(Route(14, listOf(), "Alcantarilla - Torres de Cotillas - Molina de Segura"))
        routes.add(Route(21, listOf(), "La Basca - Beniel - Orilla del Azarbe - Murcia"))
        routes.add(Route(22, listOf(), "Beniel - Santomera - Murcia"))
        routes.add(Route(26, listOf(), "El Palmar — Murcia"))
        routes.add(Route(28, listOf(), "Sangonera la Verde — Murcia"))
        routes.add(Route(29, listOf(), "La Alberca — Murcia (Por Patiño)"))
        routes.add(Route(30, listOf(), "Los Ramos — Murcia"))
        routes.add(Route(31, listOf(), "Beniel - El Raal — Alquerias — Murcia"))
        routes.add(Route(32, listOf(), "Cruce El Raal — Murcia"))
        routes.add(Route(36, listOf(), "Santomera — Murcia"))
        routes.add(Route(37, listOf(), "El Bojar — Secano — Murcia"))
        routes.add(Route(39, listOf(), "Campus Universiatio — Murcia"))
        routes.add(Route(44, listOf(), "Alcantarilla — Espinardo"))
        routes.add(Route(50, listOf(), "Agezares — Murcia — Cabezo de Torres"))
        routes.add(Route(62, listOf(), "Rincon de Seca — Murcia — Orilla del Azarbe"))
        routes.add(Route(70, listOf(), "Avileses — Murcia"))
        routes.add(Route(72, listOf(), "Los Conesas — Valladolises — Corvera — Murcia"))
        routes.add(Route(78, listOf(), "Beniajan — El Palmar — Campus de Espinardo"))
        routes.add(Route(79, listOf(), "Javali Viejo — Murcia"))
        routes.add(Route(91, listOf(), "Sangonera la Seca — Javali Nuevo — Murcia"))

        val routeAdapter = RouteAdapter(routes, routeClickListener)
        binding.recyclerLineas.adapter = routeAdapter

    }
}