package com.example.pathfitx;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// GetHeight loads the height/weight layout and is ready to host logic.
public class GetHeight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Links this Activity class to the visual layout defined in res/layout/get_height.xml.
        setContentView(R.layout.activity_get_height);

        // Initialization code for EditTexts, Buttons, etc., goes here.
    }
}