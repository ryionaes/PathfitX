package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetInfo extends AppCompatActivity {

    private EditText ageEditText;

    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);

        ageEditText = findViewById(R.id.ageEditText);
        ImageButton backBtn = findViewById(R.id.backButton);
        Button nextBtn = findViewById(R.id.nextButton);

        nextBtn.setOnClickListener(v -> {
            String age = ageEditText.getText().toString();

            if (age.isEmpty()) {
                Toast.makeText(GetInfo.this, "Please enter your age", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("AGE", age);
            editor.apply();

            Intent intent = new Intent(GetInfo.this, GetHeight.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener(v -> {
            // Navigate back to GetGoals
            Intent intent = new Intent(GetInfo.this, GetGoals.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        setupLocationSpinner();
    }

    private void setupLocationSpinner() {
        String[] locationData = getResources().getStringArray(R.array.location_array);
        List<String> combinedList = new ArrayList<>(Arrays.asList(locationData));
        String[] finalLocations = combinedList.toArray(new String[0]);

        Spinner locationSpinner = findViewById(R.id.country_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_black_text,
                finalLocations
        );

        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item_black_text
        );

        locationSpinner.setAdapter(adapter);

        if (finalLocations.length > 0) {
            locationSpinner.setSelection(0);
        }
    }
}
