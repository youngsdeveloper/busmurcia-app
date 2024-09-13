package com.youngsdeveloper.bus_murcia.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.databinding.FragmentNewPlaceBinding
import com.youngsdeveloper.bus_murcia.local.AppDatabase
import com.youngsdeveloper.bus_murcia.models.Place
import com.youngsdeveloper.bus_murcia.models.toPlaceEntity
import io.nlopez.smartlocation.SmartLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class NewPlaceFragment : Fragment(R.layout.fragment_new_place) {


    private lateinit var map_new_place:MapView
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1;


    private var location_picked: Marker? = null

    private lateinit var appDatabase: AppDatabase


    private var _binding: FragmentNewPlaceBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaceBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Load Database
        appDatabase = AppDatabase
                .getDatabase(requireContext())

        // Load Map

        this.map_new_place = binding.map


        map_new_place.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        binding.map.setMultiTouchControls(true)

        val mapController = binding.map.controller
        mapController.setZoom(15.0)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.map);
        locationOverlay.enableMyLocation();
        binding.map.overlays.add(locationOverlay)

        var startPoint = GeoPoint(37.9901166, -1.1345895);


        checkLocationPermission()
        SmartLocation.with(context).location()
                .oneFix()
                .start { location ->
                    location?.let { location ->
                        startPoint = GeoPoint(location.latitude, location.longitude);
                        mapController.setCenter(startPoint);
                    }

                }



        mapController.setCenter(startPoint);


        val mReceive = (object: MapEventsReceiver{
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {



                location_picked?.let { location_picked ->
                    location_picked.remove(binding.map)
                }

                val marker = Marker(binding.map)
                marker.position = p
                marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_map)
                marker.title = "Ubicación seleccionada"
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                binding.map.overlays.add(marker)
                binding.map.invalidate()
                location_picked = marker

                checkButtonCreatePlaceEnabled()


                return true

            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return true
            }

        })

        val overlayEvents = MapEventsOverlay(mReceive);
        binding.map.overlays.add(overlayEvents);

        binding.textPlaceName.addTextChangedListener {
            checkButtonCreatePlaceEnabled()
        }

        binding.buttonCreatePlace.setOnClickListener {
            if(checkButtonCreatePlaceEnabled()){

                CoroutineScope(Dispatchers.IO).launch {

                    val place = Place(-1, binding.textPlaceName.text.toString(), location_picked?.position!!.latitude, location_picked?.position!!.longitude)
                    appDatabase.placeDao()
                            .savePlace(place.toPlaceEntity())

                    requireActivity().runOnUiThread {
                        findNavController().popBackStack()
                    }
                }

            }
        }
    }

    private fun checkButtonCreatePlaceEnabled():Boolean{
        val check = location_picked!=null && (binding.textPlaceName.text?.isNotBlank() == true)
        binding.buttonCreatePlace.isEnabled = check
        return check

    }

    override fun onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().load(requireContext(), prefs);
        map_new_place.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().save(requireContext(), prefs);
        map_new_place.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toTypedArray(),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
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


    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

}