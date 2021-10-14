package com.juice_studio.busmurciaapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.models.Place
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment : Fragment(R.layout.fragment_places) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_media_legua.setOnClickListener{
            val place = Place("Media Legua (Nonduermas)", 37.972487, -1.172476)
            val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place)
            findNavController().navigate(action)
        }

        button_la_vereda.setOnClickListener {
            val place = Place("La Vereda (Aljucer)", 37.955330, -1.151101)
            val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place)
            findNavController().navigate(action)

        }

        button_media_legua_raya.setOnClickListener {
            val place = Place("Media Ñegua (La Raya)", 37.973143, -1.172564)
            val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place)
            findNavController().navigate(action)

        }

    }

}