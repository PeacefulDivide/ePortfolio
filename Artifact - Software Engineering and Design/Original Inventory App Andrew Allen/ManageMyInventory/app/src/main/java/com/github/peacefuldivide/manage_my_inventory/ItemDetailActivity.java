package com.github.peacefuldivide.manage_my_inventory;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Manages individual item details
 * supports CRUD operations
 * and SMS alert preferences
 */
public class ItemDetailActivity  extends AppCompatActivity {

    // UI components for entry and display
    private TextInputEditText quantityEditText;
    private EditText nameEditText, thresholdEditText;
    private MaterialSwitch alertSwitch;
    private Button incrementBtn, decrementBtn, saveBtn;

    // Logic and Data members
    private DatabaseHelper dbHelper;
    private int currentQuantity = 0;
    private int itemId;

    /**
     * Initializes Barcode scanner
     * Updates name field with scanned information
     */

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    // Pre-fill the name field on the detail screen
                    nameEditText.setText(result.getContents());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);

        // Initialize Toolbar and back navigation
        MaterialToolbar toolbar = findViewById(R.id.materialToolbarAddItem);
        toolbar.setNavigationOnClickListener(v -> {
            // Returns to inventory when back arrow is pressed
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Initialize views
        nameEditText = findViewById(R.id.editTextName);
        quantityEditText = findViewById(R.id.editTextQuantity);
        thresholdEditText = findViewById(R.id.editTextThreshold);
        alertSwitch = findViewById(R.id.alertSwitch);
        incrementBtn = findViewById(R.id.incrementButton);
        decrementBtn = findViewById(R.id.decrementButton);
        saveBtn = findViewById(R.id.saveButton);

        // Get data from inventory list
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        nameEditText.setText(getIntent().getStringExtra("ITEM_NAME"));
        currentQuantity = getIntent().getIntExtra("ITEM_QTY", 0);
        quantityEditText.setText(String.valueOf(currentQuantity));
        thresholdEditText.setText(String.valueOf(getIntent().getIntExtra("ITEM_THRESHOLD", 0)));

        // Load SMS switch state from database
        int savedAlertStatus = getIntent().getIntExtra("ITEM_ALERT_STATUS", 0);
        alertSwitch.setChecked(savedAlertStatus == 1);

        // EditText listener to save manual quantity values
        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        currentQuantity = Integer.parseInt(s.toString());

                    } catch (NumberFormatException e) {
                        currentQuantity = 0;
                    }
                }
            }
        });

        // Incrementing logic
        incrementBtn.setOnClickListener(v -> {
            currentQuantity++;
            quantityEditText.setText(String.valueOf(currentQuantity));
        });

        // Decrementing logic
        decrementBtn.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                quantityEditText.setText(String.valueOf(currentQuantity));
            }
        });

        /**
         * Save button logic
         * validates input, saves to database, and checks
         * if low stock SMS alert should trigger.
         */
        saveBtn.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString().trim();
            String thresholdStr = thresholdEditText.getText().toString().trim();

            // Validate that required fields are not empty
            if (updatedName.isEmpty() || thresholdStr.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int updatedThreshold = Integer.parseInt(thresholdStr);

                // Initialize alertEnabled
                int alertEnabled = alertSwitch.isChecked() ? 1 : 0;

                // Update database using the DatabaseHelper's persistent update method
                boolean success = dbHelper.updateItem(itemId, updatedName, currentQuantity, updatedThreshold, alertEnabled);

                if (success) {
                    // Trigger SMS alert if quantity is at or below the user-defined threshold
                    if (alertSwitch.isChecked() && currentQuantity <= updatedThreshold) {
                        sendLowStockSms(updatedName);
                    }
                    Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT).show();
                    // Close activity and return to list view
                    finish();
                } else {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid threshold value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Inflates the scanner menu icon into the detail screen toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    /**
     * Handles the scanner icon click event.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_scan) {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scan barcode for item name");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            barcodeLauncher.launch(options);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback fo the runtime SMS permission request
     * Tries sending SMS if user grants permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendLowStockSms(nameEditText.getText().toString().trim());
        }
    }

    /**
     * Dispatches SMS alert using SmsManager
     * Works with Android 13+
     *
     */
    private void sendLowStockSms(String itemName) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Updated way to get SmsManager for Android 13+
                SmsManager smsManager = this.getSystemService(SmsManager.class);

                String message = "MMI Alert: " + itemName + " has reached low stock levels!";

                // Phone number for SMS , update to your number if you install the app on your personal phone.
                // TODO implement user input for phone number in future version
                smsManager.sendTextMessage("+18885559999", null, message, null, null);

                Toast.makeText(this, "Low stock SMS sent!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "SMS Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                android.util.Log.e("SMS_FAILED", "Error sending SMS", e);
            }
        } else {
            // Request runtime permissions if not granted
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 101);
        }
    }
}


