package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.RouteAdapter
import com.juice_studio.busmurciaapp.adapters.RouteClickListener
import com.juice_studio.busmurciaapp.models.Route
import com.juice_studio.busmurciaapp.models.Stop
import kotlinx.android.synthetic.main.fragment_lineas.*

class LineasFragment : Fragment(R.layout.fragment_lineas) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val routeClickListener = object : RouteClickListener {
            override fun onRouteClick(r: Route, s: Stop?) {



            }
        }

        val routes = mutableListOf<Route>()

        routes.add(Route(1, listOf(), "San Ginés — Murcia"))
        routes.add(Route(6, listOf(), "La Alberca — Murcia"))
        routes.add(Route(7, listOf(), "La Albatalia — Murcia — La Arboleja"))
        routes.add(Route(26, listOf(), "El Palmar — Murcia"))
        routes.add(Route(28, listOf(), "Sangonera la Verde — Murcia"))
        routes.add(Route(29, listOf(), "La Alberca — Murcia (Por Patiño)"))
        routes.add(Route(30, listOf(), "Los Ramos — Murcia"))
        routes.add(Route(31, listOf(), "El Raal — Alquerias — Murcia"))
        routes.add(Route(32, listOf(), "Cruce El Raal — Murcia"))
        routes.add(Route(36, listOf(), "Cobatillas — Murcia"))
        routes.add(Route(37, listOf(), "El Bojar — Secano — Murcia"))
        routes.add(Route(39, listOf(), "Campus Universiatio — Murcia"))
        routes.add(Route(44, listOf(), "Barriomar — Espinardo"))
        routes.add(Route(50, listOf(), "Agezares — Murcia — Cabezo de Torres"))
        routes.add(Route(62, listOf(), "Rincon de Seca — Murcia — Orilla del Azarbe"))
        routes.add(Route(70, listOf(), "Avileses — Murcia"))
        routes.add(Route(72, listOf(), "Los Conesas — Valladolises — Corvera — Murcia"))
        routes.add(Route(78, listOf(), "Beniajan — El Palmar — Campus de Espinardo"))
        routes.add(Route(79, listOf(), "Javali Viejo — Murcia"))
        routes.add(Route(91, listOf(), "Sangonera la Seca — Javali Nuevo — Murcia"))

        val routeAdapter = RouteAdapter(routes, routeClickListener)
        recycler_lineas.adapter = routeAdapter

    }
}