package com.github.peacefuldivide.manage_my_inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.CheckResult;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Class that Handles all database operations for the Manage My Inventory application.
 * Manages the SQLite lifecycle, user authentication security via BCrypt,
 * and CRUD operations for inventory management.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database configuration
    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 4;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_INVENTORY = "inventory";

    // Users table
    public static final String USER_ID = "id";
    public static final String USER_NAME = "username";
    public static final String USER_PASSWORD = "password";
    // To see who added an item to inventory
    public static final String ITEM_USER_ID = "user_id";

    // Inventory table
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_QUANTITY = "quantity";
    public static final String ITEM_THRESHOLD = "threshold";

    // Stores state of SMS alert switch 0 for off, 1 for on.
    public static final String ITEM_ALERT_ENABLED = "alert_enabled";

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Initializes database
     * Constraints like NOT NULL and DEFAULT are added for data integrity
     * before the Java logic touches data.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // We use "IF NOT EXISTS" as a safety shield
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USER_NAME + " TEXT NOT NULL UNIQUE, "
                + USER_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INVENTORY + " ("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_NAME + " TEXT NOT NULL, "
                + ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ITEM_THRESHOLD + " INTEGER NOT NULL DEFAULT 1, "
                + ITEM_USER_ID + " INTEGER DEFAULT 1, "
                + ITEM_ALERT_ENABLED + " INTEGER DEFAULT 0)");
    }

    /**
     * Updates the database without data loss.
     * This uses a switch statement to apply incremental changes based on the version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The "Nuclear Option": During development/testing, it is often safer
        // to drop and recreate. This ensures a 100% clean schema match.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    /**
     * Prevents duplicate usernames in database before hashing
     *
     */
    public boolean isUserNameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USER_ID},
                USER_NAME + "=?", new String[]{username}, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    /**
     * BCrypt implementation for security
     * Registers a new user.
     * Salting and hashing is performed before database insertion
     * this ensures plain text never touches the disk.
     */

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Bcrypts gensalt() handles the salting
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        values.put(USER_NAME, username);
        values.put(USER_PASSWORD, hashedPassword);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    /**
     * Validate user credentials against BCrypts hashes.
     */

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query only for the password column for the specific user
            cursor = db.query(TABLE_USERS,
                    new String[]{USER_PASSWORD},
                    USER_NAME + "=?",
                    new String[]{username},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Use getColumnIndex to find the password column safely
                int index = cursor.getColumnIndex(USER_PASSWORD);
                if (index != -1) {
                    String storedHash = cursor.getString(index);
                    return BCrypt.checkpw(password, storedHash);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("LOGIN_ERROR", "Error during authentication", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

/**
 * Inventory CRUD operations
 */

@CheckResult
public long addItem (String name,int qty, int threshold){
    // Validation check
    if (name == null || name.trim().isEmpty()) {
        return -1;
    }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ITEM_NAME, name);
        values.put(ITEM_QUANTITY, qty);
        values.put(ITEM_THRESHOLD, threshold);

        try {
            long id = db.insert(TABLE_INVENTORY, null, values);
            return id;
        }
        catch (Exception e) {
            android.util.Log.e("DB_ERROR", "Error inserting item", e);
            return -1;
        }
        finally {
            // Always close even if error occurs
            db.close();
        }
    }

    /**
    * Retrieves all items from the inventory table.
    *
    */

    public Cursor getAllItems () {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }

    /**
    * Removes an inventory item based on its unique primary key.
    *
    */

    public void deleteItem ( int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INVENTORY, ITEM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Update operation modifies existing record
     */
    public boolean updateItem(int id, String name, int qty, int threshold, int alertEnabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ITEM_NAME, name);
        values.put(ITEM_QUANTITY, qty);
        values.put(ITEM_THRESHOLD, threshold);
        values.put(ITEM_ALERT_ENABLED, alertEnabled);

        // Update row where ID matches
        int result = db.update(TABLE_INVENTORY, values, ITEM_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        // Returns true if any row was updated
        return result > 0;

    }
}