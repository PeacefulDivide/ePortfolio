package com.alttabtechnologies.inventorytracker.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.alttabtechnologies.inventorytracker.repository.InventoryRepository
import com.alttabtechnologies.inventorytracker.repository.MockInventoryRepository

/**
 * Modern Object companion for ViewModel Factory Configuration.
 */

object InventoryViewModelFactory {

    // Single fallback repository instance to preserve state across fragments when testing.
    private val defaultRepository: InventoryRepository by lazy { MockInventoryRepository() }

    // Creates a ViewModel factory using Jetpack DSL.
    fun create(repository: InventoryRepository = defaultRepository): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            InventoryViewModel(repository)
        }
    }
}