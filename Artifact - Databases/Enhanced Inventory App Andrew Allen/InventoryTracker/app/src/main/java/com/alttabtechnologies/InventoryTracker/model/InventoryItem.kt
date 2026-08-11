package com.alttabtechnologies.inventorytracker.model

import androidx.room3.ColumnInfo
import androidx.room3.Index
import androidx.room3.PrimaryKey
import androidx.room3.Entity

/**
 * Entity model representing an inventory item stored in the Room database.
 * Using Kotlin data class provides built-in safety across asynchronous threads.
 */

@Entity(
    tableName = "inventory_table",
)
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sku: String,
    val quantity: Int,
    val price: Double,
    @ColumnInfo(name = "low_stock_threshold")
    val threshold: Int
) {
    // Helper to check if stock falls below minimum threshold
    // Custom getters are ignored by Room during column mapping.
    val isLowStock: Boolean
        get() = quantity <= threshold
}
