package com.example.pathfitx;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class activity_get_info extends AppCompatActivity {

    // Tinanggal ang RadioGroup, RadioButton, at CardView declarations dahil wala nang color logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);

        // Wala na ang code para sa gender selection listener

        // 4. Setup ng Spinner
        setupLocationSpinner();
    }

    // Tinanggal ang updateGenderSelection() method

    // ---------------------------------------------------------------------------------------------
    // SPINNER LOGIC (Kept as is)
    // ---------------------------------------------------------------------------------------------
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