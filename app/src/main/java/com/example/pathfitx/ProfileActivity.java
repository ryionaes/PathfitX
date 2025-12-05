package com.example.pathfitx;

import android.content.Intent; // Import needed for navigation
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Import Button
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        // --- 1. SETUP DROPDOWNS (Existing Code) ---
        setupDropdown(R.id.btnNotifications, R.id.contentNotifications);
        setupDropdown(R.id.btnSettings, R.id.contentHelp);
        setupDropdown(R.id.btnPrivacy, R.id.contentAbout);

        // --- 2. SETUP "ACCOUNT INFORMATION" BUTTON ---
        // When clicked, go to the GetUser screen to edit details
        View btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, GetUser.class);
                // PASS THE FLAG: Tells GetUser that the user is coming from the Profile screen (edit mode)
                intent.putExtra("NAV_SOURCE", "PROFILE");
                startActivity(intent);
            }
        });

        // --- 3. SETUP "LOG OUT" BUTTON ---
        // When clicked, go back to the Main (Get Started) screen
        View btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                // This clears the history so the user can't press "Back" to return to the profile
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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