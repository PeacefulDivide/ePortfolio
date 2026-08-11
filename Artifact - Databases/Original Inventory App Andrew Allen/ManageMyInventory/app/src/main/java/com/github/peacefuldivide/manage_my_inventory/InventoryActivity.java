package com.github.peacefuldivide.manage_my_inventory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Main inventory screen that displays grid of items.
 * Handles database retrieval, barcode scanning,
 * and adding items.
 */
public class InventoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<InventoryItem> itemList;

    /**
     * Launcher handles intents from scanner activity
     */
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    // Fill with scanned upc
                    showAddItemDialog(result.getContents());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        // Prevent searchbar from overlapping
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FAB for manual item entry
        FloatingActionButton fab = findViewById(R.id.addItem);
        fab.setOnClickListener(v -> showAddItemDialog(""));

        // Database and data structure setup
        dbHelper = new DatabaseHelper(this);
        itemList = new ArrayList<>();

        // Set grid layout with two columns
        recyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        // Initial data pull
        loadData();
    }

    /**
     * Refreshes inventory list when users returns to it
     */

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * Inflates top menu with scan action
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    /**
     * Handles top app bar button clicks
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            checkCameraPermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Permission check at runtime for camera access
     */

    private void checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            startScanner();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScanner();
        }
    }

    /**
     * Configures and launches zxing barcode scanner
     */

    private void startScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan an item barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        // Uses capture activity
        barcodeLauncher.launch(options);
    }

    /**
     * Syncs recycler view with current state of database
     */

    private void loadData() {
        itemList.clear();
        Cursor cursor = dbHelper.getAllItems();

        // Iterates through database cursor to populate list
        if (cursor.moveToFirst()) {
            do {
                itemList.add(new InventoryItem(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                ));
            } while (cursor.moveToNext());
        }
        // Always close to prevent memory leaks
        cursor.close();

        // Bind data to adapter and intialize if first run
        if (adapter == null) {
            adapter = new InventoryAdapter(itemList);
            recyclerView.setAdapter(adapter);
        } else {
            // Notify adapter when dataset changes
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Displays custom alert dialog to input new item details
     */
    private void showAddItemDialog(String initialName) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        // Inflate custom layout for dialog
        final android.view.View customLayout = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        builder.setView(customLayout);

        EditText nameInput = customLayout.findViewById(R.id.edit_item_name);
        EditText qtyInput = customLayout.findViewById(R.id.edit_item_qty);
        EditText thresholdInput = customLayout.findViewById(R.id.edit_item_threshold);

        // Fill name with scanner input
        nameInput.setText(initialName);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String qtyStr = qtyInput.getText().toString().trim();
            String thresholdStr = thresholdInput.getText().toString().trim();

            // Validate inputs and commit to database
            if (!name.isEmpty() && !qtyStr.isEmpty() && !thresholdStr.isEmpty()) {
                try {
                    dbHelper.addItem(name, Integer.parseInt(qtyStr), Integer.parseInt(thresholdStr));
                    loadData();
                    Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}