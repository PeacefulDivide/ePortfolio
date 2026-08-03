package com.alttabtechnologies.inventorytracker.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alttabtechnologies.inventorytracker.R
import com.alttabtechnologies.inventorytracker.databinding.ActivityMainBinding
import com.alttabtechnologies.inventorytracker.databinding.NavHeaderInventoryBinding
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModel
import com.alttabtechnologies.inventorytracker.viewmodel.InventoryViewModelFactory
import java.text.NumberFormat


/**
 * Main View Layer of the application.
 * Manages UI interactions and observes ViewModel streams.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    // Activity scoped ViewModel providing state stream across fragments.
    private val viewModel: InventoryViewModel by viewModels {
        InventoryViewModelFactory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // Enable edge to edge for system bars.
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // Setup layout binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize NavController.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Connect navigation drawer to NavController.
        binding.navigationView.setupWithNavController(navController)

        // Live header updates.
        setupDrawerHeaderObservers()

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

    // Helper function to let fragments open the nav drawer.
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    // Inflates and observes metrics bound to drawer header.
    private fun setupDrawerHeaderObservers() {
        if (binding.navigationView.headerCount > 0) {
            val headerView = binding.navigationView.getHeaderView(0)
            val headerBinding = NavHeaderInventoryBinding.bind(headerView)

            // Observe reactive total inventory calculation and format as currency.
            viewModel.totalInventoryValue.observe(this) { value ->
                val formatted = NumberFormat.getCurrencyInstance().format(value ?: 0.0)
                headerBinding.tvTotalValue.text = "Total Value: $formatted"
            }

            // Observe real time count of items falling below inventory threshold.
            viewModel.lowStockCount.observe(this) { count ->
                headerBinding.tvLowStockAlert.text = "Low Stock Items: ${count ?: 0}"
            }
        }
    }
}
