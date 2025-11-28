package com.example.pathfitx;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class get_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_info);


        String[] locationData = getResources().getStringArray(R.array.location_array);

        List<String> combinedList = new ArrayList<>();

        combinedList.addAll(Arrays.asList(locationData));

        String[] finalLocations = combinedList.toArray(new String[0]);

        Spinner locationSpinner = findViewById(R.id.country_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                // Use your custom layout for the selected item view
                R.layout.spinner_item_black_text,
                finalLocations
        );

        adapter.setDropDownViewResource(
                // Use your custom layout for the dropdown list view
                R.layout.spinner_dropdown_item_black_text
        );

        locationSpinner.setAdapter(adapter);

        if (finalLocations.length > 0) {
            locationSpinner.setSelection(0);
        }
    }
}