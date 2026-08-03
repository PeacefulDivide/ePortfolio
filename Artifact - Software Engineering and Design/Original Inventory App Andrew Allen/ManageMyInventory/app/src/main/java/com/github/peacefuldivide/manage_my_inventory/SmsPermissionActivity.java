package com.github.peacefuldivide.manage_my_inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Intermediate screen that handles SMS permission requests
 * This lets the user know they need SMS for
 * low inventory alerts
 */

public class SmsPermissionActivity extends AppCompatActivity {

    // Unique identifier for SMS permission callback
    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        Button grantButton = findViewById(R.id.btn_grant_permission);
        Button skipButton = findViewById(R.id.btn_skip_permission);

        /**
         * Logic for grant button
         * Checks if permission is already granted
         * triggers system dialog if not.
         */
        grantButton.setOnClickListener(v -> {
            // Check if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED) {
                // If granted skip dialog
                proceedToInventory();
            }
            else {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            }
        });

        // Skip allows app to remain functional without SMS permission
        skipButton.setOnClickListener(v -> proceedToInventory());
    }

    /**
     * Callback method triggers after user interacts with system permission dialog
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();

        }
        // Regardless of choice move to inventory screen
        proceedToInventory();
    }

    /**\
     * Transitions to inventory screen
     */
    private void proceedToInventory() {
        Intent intent = new Intent (SmsPermissionActivity.this, InventoryActivity.class);
        startActivity(intent);
        finish();
    }
}