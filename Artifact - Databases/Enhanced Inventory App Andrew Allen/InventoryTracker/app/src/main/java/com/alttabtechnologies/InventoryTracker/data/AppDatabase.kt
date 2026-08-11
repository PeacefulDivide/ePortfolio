package com.alttabtechnologies.inventorytracker.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.alttabtechnologies.inventorytracker.model.InventoryItem

/**
 * Main Room database class for inventory app.
 * Serves as the primary access point for the SQLite persist storage.
 */
@Database(
    entities = [InventoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // Connects database to Inventory Data access object.
    // Room auto generates concrete implementation at compile time.
    abstract fun inventoryDao(): InventoryDao

    companion object {
        // @Volatile ensures changes to INSTANCE are immediate across all threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns thread safe singleton instance of [AppDatabase].
        // This prevents multiple database instances from being opened at one time.
        fun getDatabase(context: Context): AppDatabase {

            // Return existing instance if exists, otherwise sync thread access to create it.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventory_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}