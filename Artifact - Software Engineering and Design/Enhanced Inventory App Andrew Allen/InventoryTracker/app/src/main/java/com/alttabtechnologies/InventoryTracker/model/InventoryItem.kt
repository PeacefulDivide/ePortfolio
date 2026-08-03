package com.alttabtechnologies.inventorytracker.model

/**
 * Immutable domain model representing an inventory item.
 * Using Kotlin data class provides built-in safety across asynchronous threads.
 */

data class InventoryItem(
    val id: Int = 0,
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Double,
    val threshold: Int
) {
    // Helper to check if stock falls below minimum threshold
    val isLowStock: Boolean
        get() = quantity <= threshold
}
