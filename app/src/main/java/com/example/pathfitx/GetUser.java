package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import for ImageButton
import android.widget.Toast; // Import for showing messages

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class GetUser extends AppCompatActivity {

    // Define constants for the navigation keys
    private static final String KEY_NAV_SOURCE = "NAV_SOURCE";
    private static final String SOURCE_PROFILE = "PROFILE";

    // Define variables for the UI elements
    ImageButton backButton;
    Button nextButton;
    EditText getUser;

    // Variable to hold the source of navigation
    private String navigationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user);

        // Retrieve the navigation source flag from the Intent
        // If coming from ProfileActivity, it will have the "PROFILE" source.
        navigationSource = getIntent().getStringExtra(KEY_NAV_SOURCE);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        getUser = findViewById(R.id.inputText);

        // --- Back Button Logic ---
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If editing from Profile, go back to Profile. Otherwise, go to MainActivity.
                if (SOURCE_PROFILE.equals(navigationSource)) {
                    finish(); // Simply closes GetUser and returns to the previous activity (ProfileActivity)
                } else {
                    Intent intent = new Intent(GetUser.this, MainActivity.class);
                    // Flags ensure proper back stack management
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // --- Next/Save Button Logic ---
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the user input
                String username = getUser.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(GetUser.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution if input is empty
                }

                // If coming from ProfileActivity (edit mode)
                if (SOURCE_PROFILE.equals(navigationSource)) {
                    // 1. Save the updated username (You'll need to implement actual data saving here, e.g., SharedPreferences)
                    // For now, let's just show a toast:
                    Toast.makeText(GetUser.this, "Username saved: " + username, Toast.LENGTH_SHORT).show();

                    // 2. Return to the ProfileActivity
                    finish();

                } else {
                    // If coming from MainActivity (new user flow)
                    // 1. Pass the username to the next screen (GetGoals)
                    Intent intent = new Intent(GetUser.this, GetGoals.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
            }
        });

        // --- System Back Button Logic (OnBackPressed) ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Use the same logic as the ImageButton: return to Profile if editing, otherwise MainActivity
                if (SOURCE_PROFILE.equals(navigationSource)) {
                    finish();
                } else {
                    Intent intent = new Intent(GetUser.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // --- UI Adjustment for Edit Mode ---
        if (SOURCE_PROFILE.equals(navigationSource)) {
            // Optional: Change button text from "Next" to "Save" when editing
            nextButton.setText("SAVE");
            // Optional: Pre-fill the EditText with the current user's name (requires passing current name from ProfileActivity)
            // Example: getUser.setText("Joshua");
        }
    }
}