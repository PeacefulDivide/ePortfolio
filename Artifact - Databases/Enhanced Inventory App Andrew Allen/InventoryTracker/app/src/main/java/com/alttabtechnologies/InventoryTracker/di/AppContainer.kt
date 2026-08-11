package com.alttabtechnologies.inventorytracker.di

import android.content.Context
import com.alttabtechnologies.inventorytracker.repository.InventoryRepository
import com.alttabtechnologies.inventorytracker.repository.RoomInventoryRepository
import com.alttabtechnologies.inventorytracker.data.AppDatabase

/**
 * Dependency injection container interface defining singletons and dependencies.
 * This allows swapping Room ORM with mock implementations during automated testing.
 */
interface AppContainer {

    // Central inventory repository instance across the app.
    val inventoryRepository: InventoryRepository
}

// Main implementation of [App container] that manages concrete dependencies.
class DefaultAppContainer(private val context: Context) : AppContainer {

    // Initialize Room database instance.
    // Thread safety and lazy on first access.
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    // Provide concrete Room Repository to ViewModels
    override val inventoryRepository: InventoryRepository by lazy {
        RoomInventoryRepository(database.inventoryDao())
    }
}