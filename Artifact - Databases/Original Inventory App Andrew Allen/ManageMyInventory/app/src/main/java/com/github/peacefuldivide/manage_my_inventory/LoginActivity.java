package com.github.peacefuldivide.manage_my_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Authentication screen that handles user
 * registration and login.
 * This is the entry point for the app.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton, registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // System bar insets for getting status bar and nav bar to play nicely
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Database helper
        dbHelper = new DatabaseHelper(this);

        // Bind UI components
        usernameInput = findViewById(R.id.editUsername);
        passwordInput = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set up register logic
        // Captures credentials to create a record
        registerButton.setOnClickListener(v -> {
            String user = usernameInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            // Validation for non empty fields
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();

            }
            else {
                // Hashes password before insertion into database
                if (dbHelper.addUser(user, pass)) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(this, "User already exists or error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * Login logic
         * Authenticates user against stored hashes
         */
        loginButton.setOnClickListener(v -> {
            String user = usernameInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();

            // Safety guard to prevent queries on empty input
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return; // Stop the execution here
            }

            // try catch to catch any BCrypt or Database issues
            try {
                if (dbHelper.checkUser(user, pass)) {
                    Intent intent = new Intent(LoginActivity.this, InventoryActivity.class);
                    startActivity(intent);
                    // Removes ability for user to back track to login screen after successful login
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // Debugging for crashes
                Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                android.util.Log.e("LOGIN_CRASH", "Error during login", e);
            }
        });
    }
}
