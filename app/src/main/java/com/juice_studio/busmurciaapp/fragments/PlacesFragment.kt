package com.juice_studio.busmurciaapp.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.juice_studio.busmurciaapp.R
import com.juice_studio.busmurciaapp.adapters.PlaceAdapter
import com.juice_studio.busmurciaapp.adapters.PlaceClickListener
import com.juice_studio.busmurciaapp.local.AppDatabase
import com.juice_studio.busmurciaapp.local.toPlace
import com.juice_studio.busmurciaapp.models.Place
import com.juice_studio.busmurciaapp.models.toPlaceEntity
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlacesFragment : Fragment(R.layout.fragment_places) {


    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()

                val place = Place(-1, "Tu ubicación", location.latitude, location.longitude)
                val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place, place.name)
                findNavController().navigate(action)

            }
        }
    }
    private lateinit var appDatabase: AppDatabase
    private val placesAdapter = PlaceAdapter(mutableListOf())



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        //Load Database
        appDatabase = AppDatabase
                .getDatabase(requireContext())

        // Load Places
        loadPlaces()

        placesAdapter.placeClickListener = (object:PlaceClickListener{
            override fun onPlaceClick(place: Place) {
                val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place, place.name)
                findNavController().navigate(action)
            }

            override fun onPlaceDelete(place: Place) {


                if(place.id<0){
                    return;
                }

                MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Eliminar sitio favorito")
                        .setMessage("¿Estas seguro de que deseas eliminar de tus favoritos: '" + place.name + "'?")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Eliminar sitio") { dialog, which ->
                            // Respond to positive button press
                            CoroutineScope(Dispatchers.IO).launch {

                                appDatabase.placeDao().deletePlace(place.id);

                                loadPlaces()

                                requireActivity().runOnUiThread {
                                    MaterialAlertDialogBuilder(requireContext())
                                            .setTitle("Sitio eliminado")
                                            .setMessage("El sitio ha sido eliminado de tus favoritos correctamente, puedes volver a añadirlo cuando quieras.")
                                            .setPositiveButton("De acuerdo", null)
                                            .show()
                                }

                            }
                        }
                        .show()




            }


        })

        recycler_places.adapter = placesAdapter


        button_create_place.setOnClickListener {
            findNavController().navigate(R.id.action_placesFragment_to_newPlaceFragment)
        }

        button_location.setOnClickListener {
            checkLocationPermission()
            SmartLocation.with(context).location()
                    .oneFix()
                    .start { location ->
                        val place = Place(-1, "Tu ubicación", location.latitude, location.longitude)
                        val action = PlacesFragmentDirections.actionPlacesFragmentToPlaceFragment(place, place.name)
                        findNavController().navigate(action)

                    }
        }

    }

    private fun loadPlaces(){
        CoroutineScope(Dispatchers.IO).launch {

            var places = appDatabase.placeDao().getAllPlaces()


            requireActivity().runOnUiThread {
                placesAdapter.items =places.map { placeEntity -> placeEntity.toPlace() }
                placesAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton(
                                "OK"
                        ) { _, _ ->
                            //Prompt the user once explanation has been shown
                            requestLocationPermission()
                        }
                        .create()
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }



    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                        )

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            )
                    ) {
                        startActivity(
                                Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", requireActivity().packageName, null),
                                ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                        )

                        Toast.makeText(
                                requireActivity(),
                                "Granted Background Location Permission",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireActivity(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

}