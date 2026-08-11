package com.alttabtechnologies.inventorytracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import com.alttabtechnologies.inventorytracker.repository.InventoryRepository
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

/**
 * ViewModel is responsible for managing UI related inventory data and lifecycle.
 * Acts as a bridge between the layout view layer and the data repository layer.
 */

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    // Observable stream of inventory items exposed to the UI layer.
    val allInventoryItems: LiveData<List<InventoryItem>> = repository.getAllItems().asLiveData()

    // State for the active user search query input.
    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> get() = _searchQuery

    // State for current sorting mode.
    private val _sortState = MutableLiveData<SortState>(SortState())
    val sortState: LiveData<SortState> get() = _sortState

    // MediatorLiveData combines changes from search query, sort state, and repository items.
    val filteredInventoryItems = MediatorLiveData<List<InventoryItem>>().apply {

        fun update() {
            val items = allInventoryItems.value ?: emptyList()
            val query = _searchQuery.value?.trim().orEmpty()
            val state = _sortState.value ?: SortState()

            // In memory search and filtering.
            // O(1) time complexity if empty.
            // O(n) if searching text.
            val filtered = if (query.isEmpty()) {
                items
            } else {
                items.filter { item ->
                    item.name.contains(query, ignoreCase = true) ||
                            item.sku.contains(query, ignoreCase = true)
                }
            }

            // Dynamic multi criteria chained sorting.
            // O(k * n log n) where k is the number of active sort criteria.
            value = sortInventoryItems(filtered, state)
        }

        // Evaluate list whenever any stream changes.
        addSource(allInventoryItems) { update() }
        addSource(_searchQuery) { update() }
        addSource(_sortState) { update() }
    }

    // Builds comparator based on active SortRules in SortState.
    // Check primary, secondary, and tertiary sorting rules dynamically.

    private fun sortInventoryItems(items: List<InventoryItem>, state: SortState): List<InventoryItem> {
        if (state.activeRules.isEmpty()) return items

        var comparator: Comparator<InventoryItem>? = null

        for (rule in state.activeRules) {

            // Base comparator for selected criteria.
            val baseComparator: Comparator<InventoryItem> = when (rule.criteria) {
                SortCriteria.NAME -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
                SortCriteria.QUANTITY -> compareBy { it.quantity }
                SortCriteria.PRICE -> compareBy { it.price }
            }

            // Reverse comparator direction if order is descending.
            val directedComparator = if (rule.order == SortOrder.DESCENDING) {
                baseComparator.reversed()
            } else {
                baseComparator
            }

            // Chain comparators together.
            // Primary to secondary and then tertiary.
            comparator = if (comparator == null) {
                directedComparator
            } else {
                comparator.then(directedComparator)
            }
        }

        return comparator?.let { items.sortedWith(it) } ?: items
    }

    // Data aggregations.

    // O(n) algorithm to calculate total value of all inventory items.
    val totalInventoryValue: LiveData<Double> = allInventoryItems.map { list ->
        list.sumOf { it.price * it.quantity }
    }

    // O(n) algorithm to calculate low stock alerts against threshold levels.
    val lowStockCount: LiveData<Int> = allInventoryItems.map { list ->
        list.count { it.quantity <= it.threshold }
    }

    // State update handlers.

    // Updates current search filter state triggered by SearchBar UI.
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Updates active sorting and triggers evaluation of the data stream.
    fun toggleSortDirection(criteria: SortCriteria) {
        val currentState = _sortState.value ?: SortState()
        val currentRules = currentState.activeRules.toMutableList()
        val existingIndex = currentRules.indexOfFirst { it.criteria == criteria }

        if (existingIndex != -1) {
            val existingRule = currentRules[existingIndex]
            val nextOrder = if (existingRule.order == SortOrder.ASCENDING) {

                // Reverse sort direction on tap.
                SortOrder.DESCENDING
            } else {

                // Reverse sort direction on tap again.
                SortOrder.ASCENDING
            }
            currentRules[existingIndex] = existingRule.copy(order = nextOrder)
        } else {

            // Add new rule as ascending on first tap.
            currentRules.add(SortRule(criteria = criteria, order = SortOrder.ASCENDING))
        }

        _sortState.value = SortState(activeRules = currentRules)
    }

    // Remove sort rule when user hits 'x' on chip.
    fun removeSortRule(criteria: SortCriteria) {
        val currentState = _sortState.value ?: SortState()
        val updatedRules = currentState.activeRules.filterNot { it.criteria == criteria }
        _sortState.value = SortState(activeRules = updatedRules)
    }

    // Reset all sorting rules back to default (name ascending).
    fun resetSorting() {
        _sortState.value = SortState(activeRules = emptyList())
    }

    // Event signaling to the fragment that the save operation was completed.
    private val _saveOperationComplete = MutableLiveData<Boolean>()
    val saveOperationComplete: LiveData<Boolean> get() = _saveOperationComplete

    // O(n) lookup to find item from LiveData stream and returns transformation stream that updates dynamically.
    fun getItemById(id: Int): LiveData<InventoryItem?> {
        return allInventoryItems.map { items ->
            items.find { it.id == id }
        }
    }

    // Handles routing form text edits from Detail UI layer and determines if new or an update.
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
                repository.insertItem(newItem)
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
            repository.insertItem(newItem)
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