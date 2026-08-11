package com.alttabtechnologies.inventorytracker.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Update
import androidx.room3.Query
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Data access object providing contract for SQLite database operations.
 */
@Dao
interface InventoryDao {

    // Default query that returns all items sorted with newest ID at the top.
    @Query("SELECT * FROM inventory_table ORDER BY id DESC")
    fun getAllItems(): Flow<List<InventoryItem>>

    // Retrieves single inventory item mathcing primary key ID.
    @Query("SELECT * FROM inventory_table WHERE id = :id")
    fun getItemById(id: Int): Flow<InventoryItem?>

    // Inserts new item into inventory table.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem)

    // Updates existing items fields in the inventory table with matching primary key.
    @Update
    suspend fun updateItem(item: InventoryItem)

    // Removes existing item from inventory table based on matching primary key.
    @Delete
    suspend fun deleteItem(item: InventoryItem)
}