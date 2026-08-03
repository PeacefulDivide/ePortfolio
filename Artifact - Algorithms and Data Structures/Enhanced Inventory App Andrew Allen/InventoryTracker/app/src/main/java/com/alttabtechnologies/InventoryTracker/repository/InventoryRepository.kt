package com.alttabtechnologies.inventorytracker.repository

import com.alttabtechnologies.inventorytracker.model.InventoryItem
import androidx.lifecycle.LiveData

/**
 * Data interface for managing inventory items.
 * This decouples the ViewModel layer from the actual database.
 */

interface InventoryRepository {

    // Returns a stream of all items in the inventory.
    fun getAllItems(): LiveData<List<InventoryItem>>

    // Adds a new item to the repository.
    suspend fun addItem(item: InventoryItem)

    // Updates existing item based on its unique ID.
    suspend fun updateItem(item: InventoryItem)

    // Deletes an item from the repository.
    suspend fun deleteItem(item: InventoryItem)
}