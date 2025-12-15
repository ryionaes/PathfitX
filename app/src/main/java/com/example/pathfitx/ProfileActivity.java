package com.example.pathfitx;

import android.content.Context; // Needed for SharedPreferences
import android.content.Intent;
import android.content.SharedPreferences; // Needed to clear login data
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        // --- 1. SETUP DROPDOWNS (Existing Code) ---
        setupDropdown(R.id.btnNotifications, R.id.btnNotifications);
        setupDropdown(R.id.btnSettings, R.id.contentHelp);
        setupDropdown(R.id.btnPrivacy, R.id.contentAbout);

        // --- 2. SETUP "ACCOUNT INFORMATION" BUTTON ---
        View btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, GetUser.class);
                intent.putExtra("NAV_SOURCE", "PROFILE");
                startActivity(intent);
            }
        });

        // --- 3. SETUP "LOG OUT" BUTTON (UPDATED) ---
        View btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // A. Clear the saved login data
                SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear(); // Deletes email, password, and isLoggedIn status
                editor.apply();

                // B. Go back to Login Screen
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                // C. Clear the history so they can't press "Back" to get in
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
                finish(); // Close this screen
            }
        });
    }

    private void setupDropdown(int buttonId, int contentId) {
        View button = findViewById(buttonId);
        View content = findViewById(contentId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getVisibility() == View.GONE) {
                    content.setVisibility(View.VISIBLE);
                } else {
                    content.setVisibility(View.GONE);
                }
            }
        });
    }
}