package com.alttabtechnologies.inventorytracker.data

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alttabtechnologies.inventorytracker.model.InventoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Instrumented integration unit tests for [InventoryDao] using in memory Room database
 * and verifies CRUD operations.
 */
@RunWith(AndroidJUnit4::class)
class InventoryDaoTest {

    private lateinit var inventoryDao: InventoryDao
    private lateinit var db: AppDatabase

    // Static test items with explicit primary key IDs.
    private val item1 = (InventoryItem(id = 1, name = "T-Shirt", sku = "123456",  quantity = 15, price = 19.99, threshold = 5))
    private val item2 = (InventoryItem(id = 2, name = "Monitor", sku = "789456", quantity = 5, price = 299.99, threshold = 2))
    private val item3 = (InventoryItem(id = 4, name = "Headphones", sku = "456321", quantity = 50, price = 94.99, threshold = 20))

    // Creates a fresh in memory database before running each test.
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // In memory database ensures isolation and is destroyed upon closing.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        inventoryDao = db.inventoryDao()
    }

    // Closes in memory database connection after each test completes.
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    // Verifies inserting an item persists in database.
    @Test
    @Throws(IOException::class)
    fun daoInsert_insertsItemIntoDatabase() = runBlocking {
        inventoryDao.insertItem(item1)
        val allItems = inventoryDao.getAllItems().first()

        assertEquals(1, allItems.size)
        assertEquals(item1.name, allItems[0].name)
    }

    // Verifies records are sorted by ID in DSC order.
    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsItemsInNewestFirstOrder() = runBlocking {

        // Insert all items one at a time.
        inventoryDao.insertItem(item1)
        inventoryDao.insertItem(item2)
        inventoryDao.insertItem(item3)

        val allItems = inventoryDao.getAllItems().first()

        // Verify items are ordered DSC.
        assertEquals(3, allItems.size)
        assertEquals(item3.name, allItems[0].name)
        assertEquals(item2.name, allItems[1].name)
        assertEquals(item1.name, allItems[2].name)
    }

    // Verifies updating an existing item modifies its stored field values.
    @Test
    @Throws(Exception::class)
    fun daoUpdate_updatesItemInDatabase() = runBlocking {
        inventoryDao.insertItem(item1)
        val insertedItem = inventoryDao.getAllItems().first()[0]

        // Create updated copy of inserted item.
        val updatedItem = insertedItem.copy(quantity = 50, price = 2.49)
        inventoryDao.updateItem(updatedItem)

        val retrievedItem = inventoryDao.getItemById(insertedItem.id).first()
        assertEquals(50, retrievedItem?.quantity)
        assertEquals(2.49, retrievedItem?.price ?: 0.0, 0.001)
    }

    // Verifies deleting an item removes it completely from the table.
    @Test
    @Throws(Exception::class)
    fun daoDelete_deletesItemFromDatabase() = runBlocking {
        inventoryDao.insertItem(item1)
        val insertedItem = inventoryDao.getAllItems().first()[0]

        inventoryDao.deleteItem(insertedItem)

        val allItems = inventoryDao.getAllItems().first()
        assertTrue(allItems.isEmpty())
    }
}