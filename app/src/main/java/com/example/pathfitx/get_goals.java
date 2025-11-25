package com.example.pathfitx;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity; // Import the correct base class

public class get_goals extends AppCompatActivity { // Change extends Activity to AppCompatActivity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge display (part of AndroidX)
        EdgeToEdge.enable(this);

        // Ensure this layout name (activity_get_user) is correct for your XML file
        setContentView(R.layout.get_goals);
    }
}