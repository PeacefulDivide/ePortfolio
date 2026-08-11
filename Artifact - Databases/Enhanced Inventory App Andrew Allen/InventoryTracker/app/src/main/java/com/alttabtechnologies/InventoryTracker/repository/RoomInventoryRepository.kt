package com.alttabtechnologies.inventorytracker.repository

import com.alttabtechnologies.inventorytracker.data.InventoryDao
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * Concrete implementation of [InventoryRepository] using Room ORM.
 * Acts as a clean abstraction layer between UI/ViewModel and database DAO.
 *
 * inventoryDao for performing direct SQL operations.
 */
class RoomInventoryRepository(
    private val inventoryDao: InventoryDao
) : InventoryRepository {

    // Retrieves async real time stream of all inventory items.
    override fun getAllItems(): Flow<List<InventoryItem>> = inventoryDao.getAllItems()

    // Retrieves single inventory item matching given ID as observable stream.
    override fun getItemById(id: Int): Flow<InventoryItem?> = inventoryDao.getItemById(id)

    // Inserts new inventory item or replaces existing one on primary key collision.
    override suspend fun insertItem(item: InventoryItem) = inventoryDao.insertItem(item)

    // Updates existing inventory item in local storage.
    override suspend fun updateItem(item: InventoryItem) = inventoryDao.updateItem(item)

    // Permanently remove an inventory item from local storage.
    override suspend fun deleteItem(item: InventoryItem) = inventoryDao.deleteItem(item)
}