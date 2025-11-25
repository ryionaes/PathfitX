package com.example.pathfitx;

import android.content.Intent; // 1. Import the Intent class
import android.os.Bundle;
import android.view.View; // 2. Import the View class
import android.widget.Button; // 3. Import the Button class

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Find the button using its ID
        Button getStartedButton = findViewById(R.id.button2);

        // Set an OnClickListener to handle the button press
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4. Create an Intent to switch activities
                // Intent(Context source, Class destination)
                Intent intent = new Intent(MainActivity.this, activity_get_user.class);

                // 5. Start the new Activity
                startActivity(intent);
            }
        });

        // Existing EdgeToEdge and Insets code (keep this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}