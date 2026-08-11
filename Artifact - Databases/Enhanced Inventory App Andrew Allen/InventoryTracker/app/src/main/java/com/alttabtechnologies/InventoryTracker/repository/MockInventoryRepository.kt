package com.alttabtechnologies.inventorytracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import kotlinx.coroutines.flow.*

/**
 * In-memory implementation of the repository for testing.
 * Simulates database operations entirely in memory before wiring up a real database.
 */

class MockInventoryRepository: InventoryRepository {

    private val itemList = mutableListOf<InventoryItem>()

    // Internal mutable list to hold the live data cache.
    private val _inventoryFlow = MutableStateFlow<List<InventoryItem>>(emptyList())

    // Internal LiveData that we can read and write to.
    private val inventoryLiveData = MutableLiveData<List<InventoryItem>>()

    init {

        // Seed the list with initial mock data.
        itemList.add(InventoryItem(id = 1, name = "T-Shirt", sku = "123456",  quantity = 15, price = 19.99, threshold = 5))
        itemList.add(InventoryItem(id = 2, name = "Monitor", sku = "789456", quantity = 5, price = 299.99, threshold = 2))
        itemList.add(InventoryItem(id = 4, name = "Headphones", sku = "456321", quantity = 50, price = 94.99, threshold = 20))

        // Push the list snapshot to observers
        _inventoryFlow.value = itemList.toList()
    }

    // Exposes the data stream as a read only LiveData object for the UI to observe.
    override fun getAllItems(): Flow<List<InventoryItem>> {
        return _inventoryFlow.asStateFlow()
    }

    // Implements abstract member from InventoryRepository
    override fun getItemById(id: Int): Flow<InventoryItem?> {
        return _inventoryFlow.map { list ->
            list.find { it.id == id }
        }
    }

    // Simulates an auto incrementing database ID, adds the item, and refreshes the data stream.
    override suspend fun insertItem(item: InventoryItem) {
        // Generates a simple automatic incrementing ID for mockup safety.
        val newItem = item.copy(id = (itemList.maxOfOrNull { it.id } ?: 0) +1)
        itemList.add(newItem)
        // Notify observers of the change.
        _inventoryFlow.value = itemList.toList()
    }

    // Finds the item by its ID if exists and updates its value.
    override suspend fun updateItem(item: InventoryItem) {
        val index = itemList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            itemList[index] = item
            // Updates the stream
            _inventoryFlow.value = itemList.toList()
        }
    }

    // Removes the item matching the ID from the list.
    override suspend fun deleteItem(item: InventoryItem) {
        itemList.removeIf { it.id == item.id }
        // Updates the stream
        _inventoryFlow.value = itemList.toList()
    }
}