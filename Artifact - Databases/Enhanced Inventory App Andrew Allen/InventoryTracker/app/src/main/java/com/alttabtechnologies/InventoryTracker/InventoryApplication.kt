package com.alttabtechnologies.inventorytracker

import android.app.Application
import com.alttabtechnologies.inventorytracker.di.AppContainer
import com.alttabtechnologies.inventorytracker.di.DefaultAppContainer

/**
 * Custom app class responsible for managing app dependencies.
 * Maintains single instances of database and repository objects across apps lifecycle.
 */
class InventoryApplication : Application() {

    // Apps dependency injection container instance.
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

            // Init manual DI container with app context.
            container = DefaultAppContainer(this)

    }
}