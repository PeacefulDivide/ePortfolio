package com.alttabtechnologies.inventorytracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import com.alttabtechnologies.inventorytracker.repository.InventoryRepository
import kotlinx.coroutines.launch

/**
 * ViewModel is responsible for managing UI related inventory data and lifecycle.
 * Acts as a bridge between the layout view layer and the data repository layer.
 */

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    // Observable stream of inventory items exposed to the UI layer.
    val allInventoryItems: LiveData<List<InventoryItem>> = repository.getAllItems()

    // Event signaling to the fragment that the save operation was completed.
    private val _saveOperationComplete = MutableLiveData<Boolean>()
    val saveOperationComplete: LiveData<Boolean> get() = _saveOperationComplete

    // Looks up item from LiveData stream and returns transformation stream that updates dynamically.
    fun getItemById(id: Int): LiveData<InventoryItem?> {
        return allInventoryItems.map { items ->
            items.find { it.id == id }
        }
    }

    // Handles routing form text edits from Detail UI layer and determines if new or update.
    fun updateItemDetails(id: Int, name: String, sku: String, quantity: Int, price: Double, threshold: Int) {
        viewModelScope.launch {
            if (id == -1) {

                // Construct brand new item for insertion.
                val newItem = InventoryItem(
                    name = name,
                    sku = sku,
                    quantity = quantity,
                    price = price,
                    threshold = threshold
                )
                repository.addItem(newItem)
            } else {

                // Construct update matching target ID.
                val updatedItem = InventoryItem(
                    id = id,
                    name = name,
                    sku = sku,
                    quantity = quantity,
                    price = price,
                    threshold = threshold
                )
                repository.updateItem(updatedItem)
            }

            // Update notifying background or main observers.
            _saveOperationComplete.postValue(true)
        }
    }

    // Forwards a new item creation request down to the repository.
    fun addItem(name: String, sku: String, quantity: Int, price: Double, threshold: Int) {
        viewModelScope.launch {
            val newItem = InventoryItem(
                name = name,
                sku = sku,
                quantity = quantity,
                price = price,
                threshold = threshold
            )
            repository.addItem(newItem)
            _saveOperationComplete.value = true
        }
    }

    // Forwards item modification request down to the repository.
    fun updateItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.updateItem(item)
            _saveOperationComplete.value = true
        }
    }


    // Forwards an item deletion request down to the repository.
    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
    // Resets the save state after UI consumes event.
    fun resetSaveStatus() {
        _saveOperationComplete.value = false
    }
}