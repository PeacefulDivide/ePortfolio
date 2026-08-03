package com.alttabtechnologies.inventorytracker.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.alttabtechnologies.inventorytracker.databinding.ActivityMainBinding
import com.alttabtechnologies.inventorytracker.R
import androidx.navigation.NavController

/**
 * Main View Layer of the application.
 * Manages UI interactions and observes ViewModel streams.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

        // Setup layout binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Intercept window back signals and map to Jetpack navigation.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.popBackStack()) {

                    // If nothing left to pop on nav graph close activity.
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }
}
