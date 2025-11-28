package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class activity_get_info extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);

        ImageButton backBtn;
        Button nextBtn;

        nextBtn = findViewById(R.id.nextButton);
        backBtn = findViewById(R.id.backButton);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to getUser
                Intent intent = new Intent(activity_get_info.this, activity_get_goals.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        setupLocationSpinner();

    }

    // SPINNER LOGIC (Kept as is)
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