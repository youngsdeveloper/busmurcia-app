package com.juice_studio.busmurciaapp.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.juice_studio.busmurciaapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        setTheme(R.style.Theme_BusMurciaApp)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.placesFragment, R.id.bonosFragment, R.id.lineasFragment))
        topAppBar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(topAppBar)


        topAppBar.setNavigationOnClickListener {
            if (navController.graph.startDestination == navController.currentDestination?.id) {
                finish()
            } else {
                navController.navigateUp()
            }
        }

        val bottom_navigation = bottomNavigationView
        bottom_navigation.setupWithNavController(navController)
    }


}