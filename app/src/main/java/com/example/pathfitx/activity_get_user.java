package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import for ImageButton

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_get_user extends AppCompatActivity {

    // Define variables for the UI elements
    ImageButton backButton;
     Button nextButton;
     EditText getUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        getUser = findViewById(R.id.inputText);

        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(activity_get_user.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Next Button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting the user input for next screen
                String username = getUser.getText().toString().trim();
                Intent intent = new Intent(activity_get_user.this, activity_get_goals.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
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