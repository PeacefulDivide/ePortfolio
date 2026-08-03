package com.github.peacefuldivide.manage_my_inventory;

/**
 * Data model representing a single inventory item.
 * This class acts as a container for transferring data between
 * the DatabaseHelper and UI.
 *
 */
public class InventoryItem {
    // Primary Key for database
    private int id;
    // Descriptive name of item
    private String name;
    // Current stock level
    private int quantity;
    // Minimum stock level before alert is triggered
    private int threshold;

    /**
     * Default empty constructor
     */
    public InventoryItem() {}

    /**
     * Overloaded constructor for instantiating an item with all database fields
     *
     */

    public InventoryItem(int id, String name, int quantity, int threshold) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
    }

    /**
     * Getters and Setters
     * Provides controlled access to the private data fields
     *
     */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getThreshold() { return threshold; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
}
