package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // Import for ImageButton

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_get_user extends AppCompatActivity {

    // Define variables for the UI elements
    private ImageButton backButton; // Corresponds to button1
    private Button nextButton;      // Corresponds to button2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user);

        // 1. Find the views by their IDs from the XML layout
        backButton = findViewById(R.id.button1);
        nextButton = findViewById(R.id.button2);

        // 2. Set the click listener for the BACK button (button1)
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(activity_get_user.this, MainActivity.class);
                // Use flags to clear the activity stack and prevent buildup
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });

        // 3. Set the click listener for the NEXT button (button2)
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the get_goals activity
                Intent intent = new Intent(activity_get_user.this, get_goals.class);
                startActivity(intent);
                // Do not call finish() here if you want the user to be able to press 'Back'
            }
        });


        // Existing OnBackPressed logic for the physical/system back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(activity_get_user.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}