package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences; // Import for SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet; // Import for saving goals
import java.util.Set;     // Import for saving goals

public class GetHeight extends AppCompatActivity {

    // --- UI Variables (Matching activity_get_height.xml) ---
    ImageButton backButton;
    Button nextButton;
    EditText editTextFeet, editTextInches, editTextWeight;
    MaterialButton buttonFtIn, buttonKg;

    // --- State Variables ---
    private String username;
    private ArrayList<String> selectedGoals;
    private boolean isMetricHeight = false; // False = ft/in; True = cm
    private boolean isMetricWeight = true;  // True = kg; False = lbs

    // Constant for Shared Preferences (MUST match what ProfileFragment uses)
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_height);

        // 1. Retrieve Data from previous screen (GetGoals)
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        selectedGoals = intent.getStringArrayListExtra("SELECTED_GOALS");

        // 2. Initialize UI elements
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);

        // --- Input Fields ---
        editTextFeet = findViewById(R.id.edit_text_feet);
        editTextInches = findViewById(R.id.edit_text_inches);
        editTextWeight = findViewById(R.id.edit_text_weight);

        // --- Unit Buttons ---
        buttonFtIn = findViewById(R.id.button_ft_in);
        buttonKg = findViewById(R.id.button_kg);

        // Set initial visibility/text based on default unit state
        updateHeightUnitUI();
        updateWeightUnitUI();


        // --- Unit Switching Logic ---

        buttonFtIn.setOnClickListener(v -> {
            isMetricHeight = !isMetricHeight; // Toggle state
            updateHeightUnitUI();
            editTextFeet.setText("");
            editTextInches.setText("");
        });

        buttonKg.setOnClickListener(v -> {
            isMetricWeight = !isMetricWeight; // Toggle state
            updateWeightUnitUI();
            editTextWeight.setText("");
        });


        // --- Back Button Logic ---
        backButton.setOnClickListener(v -> finish());

        // --- Next Button Logic (Validation, Conversion, SAVING, and Navigation) ---
        nextButton.setOnClickListener(v -> {
            // 1. Collect and Validate Data
            String heightTextCm = getFinalHeightValue();
            String weightTextKg = getFinalWeightValue();

            if (heightTextCm == null || heightTextCm.isEmpty() || Double.parseDouble(heightTextCm) <= 0) {
                Toast.makeText(GetHeight.this, "Please enter a valid height.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (weightTextKg == null || weightTextKg.isEmpty() || Double.parseDouble(weightTextKg) <= 0) {
                Toast.makeText(GetHeight.this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. *** CRITICAL STEP: SAVE ALL DATA TO SHARED PREFERENCES ***
            saveUserData(heightTextCm, weightTextKg);

            // 3. Navigation to the Welcome Screen
            Intent nextIntent = new Intent(GetHeight.this, WelcomePage.class);

            // Passing extras is optional now that data is saved, but you can keep it
            nextIntent.putExtra("USERNAME", username);
            startActivity(nextIntent);
            finishAffinity(); // Clear the back stack
        });
    }

    // --- NEW HELPER METHOD FOR DATA PERSISTENCE ---

    /** Saves all user data (name, goals, height, weight) to SharedPreferences. */
    private void saveUserData(String heightCm, String weightKg) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        editor.putString("USERNAME", username);
        editor.putString("HEIGHT_CM", heightCm);
        editor.putString("WEIGHT_KG", weightKg);

        // Save goals as a Set
        if (selectedGoals != null) {
            Set<String> goalsSet = new HashSet<>(selectedGoals);
            editor.putStringSet("GOALS", goalsSet);
        }

        // Flag to indicate the user has completed onboarding (optional but useful)
        editor.putBoolean("IS_ONBOARDING_COMPLETE", true);

        editor.apply(); // Write to disk
    }

    // --- Helper Methods for Unit Handling (Unchanged) ---

    /** Toggles visibility and text for height inputs based on metric state. */
    private void updateHeightUnitUI() {
        if (isMetricHeight) {
            buttonFtIn.setText("cm");
            editTextFeet.setHint("cm");
            editTextInches.setVisibility(View.GONE);
        } else {
            buttonFtIn.setText("ft/in");
            editTextFeet.setHint("ft");
            editTextInches.setHint("in");
            editTextInches.setVisibility(View.VISIBLE);
        }
    }

    /** Toggles button text for weight based on metric state. */
    private void updateWeightUnitUI() {
        if (isMetricWeight) {
            buttonKg.setText("kg");
            editTextWeight.setHint("Enter weight in kg");
        } else {
            buttonKg.setText("lbs");
            editTextWeight.setHint("Enter weight in lbs");
        }
    }

    /** Calculates and returns the height in centimeters (cm). */
    private String getFinalHeightValue() {
        String feetStr = editTextFeet.getText().toString().trim();
        String inchesStr = editTextInches.getText().toString().trim();

        if (feetStr.isEmpty() && inchesStr.isEmpty()) return "";

        try {
            if (isMetricHeight) {
                return feetStr;
            } else {
                double feet = feetStr.isEmpty() ? 0 : Double.parseDouble(feetStr);
                double inches = inchesStr.isEmpty() ? 0 : Double.parseDouble(inchesStr);

                double totalCm = (feet * 30.48) + (inches * 2.54);
                return String.valueOf(Math.round(totalCm));
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /** Calculates and returns the weight in kilograms (kg). */
    private String getFinalWeightValue() {
        String weightStr = editTextWeight.getText().toString().trim();
        if (weightStr.isEmpty()) return "";

        try {
            double weight = Double.parseDouble(weightStr);
            if (isMetricWeight) {
                return String.valueOf(weight);
            } else {
                double totalKg = weight * 0.453592;
                return String.valueOf(Math.round(totalKg * 10.0) / 10.0);
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }
}