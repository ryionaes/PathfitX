package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences; // 1. Import SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // Import Insets
import androidx.core.view.ViewCompat; // Import ViewCompat
import androidx.core.view.WindowInsetsCompat; // Import WindowInsetsCompat

public class GetUser extends AppCompatActivity {

    private static final String KEY_NAV_SOURCE = "NAV_SOURCE";
    private static final String SOURCE_PROFILE = "PROFILE";
    private static final String PREFS_NAME = "UserPrefs"; // Name of the storage file

    ImageButton backButton;
    Button nextButton;
    EditText getUser;
    private String navigationSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user);

        // Handle Window Insets (Fixes layout overlapping with system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigationSource = getIntent().getStringExtra(KEY_NAV_SOURCE);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        getUser = findViewById(R.id.inputText);

        // --- PRE-FILL DATA IF EDITING ---
        if (SOURCE_PROFILE.equals(navigationSource)) {
            nextButton.setText("SAVE");

            // Optional: If you passed the current name from ProfileActivity, show it here
            // String currentName = getIntent().getStringExtra("CURRENT_NAME");
            // if(currentName != null) getUser.setText(currentName);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getUser.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(GetUser.this, "Please enter your username.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (SOURCE_PROFILE.equals(navigationSource)) {
                    // *** ADDED: ACTUALLY SAVE THE DATA ***
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("USERNAME", username);
                    editor.apply();
                    // *************************************

                    Toast.makeText(GetUser.this, "Username saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // New User Flow
                    Intent intent = new Intent(GetUser.this, GetGoals.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
    }
}