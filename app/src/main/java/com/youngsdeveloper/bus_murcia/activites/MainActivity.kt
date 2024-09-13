package com.youngsdeveloper.bus_murcia.activites

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {


        setTheme(R.style.Theme_BusMurciaApp)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.placesFragment, R.id.bonosFragment, R.id.lineasFragment, R.id.ultimaHoraFragment))
        binding.topAppBar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.topAppBar)


        binding.topAppBar.setNavigationOnClickListener {
            if (navController.graph.startDestinationId == navController.currentDestination?.id) {
                finish()
            } else {
                navController.navigateUp()
            }
        }

        val bottom_navigation = binding.bottomNavigationView
        bottom_navigation.setupWithNavController(navController)

        val badge_bonos = bottom_navigation.getOrCreateBadge(R.id.bonosFragment)
        badge_bonos.number = 1

        val prefs = getSharedPreferences("com.youngsdeveloper.bus_murcia", MODE_PRIVATE);


        badge_bonos.isVisible = !prefs.getBoolean("has_seen_bonos", false)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bonosFragment -> {
                    if(badge_bonos.isVisible){
                        badge_bonos.isVisible = false
                        prefs.edit().putBoolean("has_seen_bonos", true).commit();
                    }

                }
            }
            NavigationUI.onNavDestinationSelected(it, navController)
            true
        }

        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
        }

        askNotificationPermission()
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {


                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.notifications_request))
                    .setPositiveButton("De acuerdo",
                        DialogInterface.OnClickListener { dialog, id ->
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        })
                    .setNegativeButton("No, gracias.",
                        DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                        })
                // Create the AlertDialog object and return it
                builder.create().show()


                // DONE: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


}