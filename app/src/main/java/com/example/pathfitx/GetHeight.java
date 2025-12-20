package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GetHeight extends AppCompatActivity {

    private static final String TAG = "GetHeightActivity";
    private static final String PREFS_NAME = "UserPrefs";
    private static final String REG_STEP_KEY = "REGISTRATION_STEP";
    private static final String ONBOARDING_COMPLETE_KEY = "IS_ONBOARDING_COMPLETE"; // Added for completion

    // UI Variables
    private ImageButton backButton;
    private Button nextButton;
    private EditText editTextFeet, editTextInches, editTextWeight;
    private MaterialButton buttonFtIn, buttonKg;

    // State Variables
    private boolean isMetricHeight = false;
    private boolean isMetricWeight = true;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_height);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize UI
        initializeViews();
        updateHeightUnitUI();
        updateWeightUnitUI();

        // Listeners
        buttonFtIn.setOnClickListener(v -> toggleHeightUnit());
        buttonKg.setOnClickListener(v -> toggleWeightUnit());
        backButton.setOnClickListener(v -> finish());
        nextButton.setOnClickListener(v -> validateAndSaveData());
    }

    private void validateAndSaveData() {
        String heightCmStr = getFinalHeightInCm();
        String weightKgStr = getFinalWeightInKg();

        if (heightCmStr == null || heightCmStr.isEmpty()) {
            Toast.makeText(this, "Please enter a valid height.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (weightKgStr == null || weightKgStr.isEmpty()) {
            Toast.makeText(this, "Please enter a valid weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user is currently logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        double height = Double.parseDouble(heightCmStr);
        double weight = Double.parseDouble(weightKgStr);

        Map<String, Object> finalData = new HashMap<>();
        finalData.put("height_cm", height);
        finalData.put("weight_kg", weight);

        db.collection("users").document(currentUser.getUid())
                .update(finalData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Final user data saved successfully!");

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(REG_STEP_KEY, "STEP_HEIGHT");
                    editor.putBoolean(ONBOARDING_COMPLETE_KEY, true); // Mark as complete here now
                    editor.apply();

                    Intent intent = new Intent(GetHeight.this, WelcomePage.class); // Changed to WelcomePage
                    startActivity(intent);
                    finishAffinity(); // Finish all previous activities
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating final data", e);
                    Toast.makeText(GetHeight.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        editTextFeet = findViewById(R.id.edit_text_feet);
        editTextInches = findViewById(R.id.edit_text_inches);
        editTextWeight = findViewById(R.id.edit_text_weight);
        buttonFtIn = findViewById(R.id.button_ft_in);
        buttonKg = findViewById(R.id.button_kg);
    }

    private void toggleHeightUnit() {
        isMetricHeight = !isMetricHeight;
        updateHeightUnitUI();
        editTextFeet.setText("");
        editTextInches.setText("");
    }

    private void toggleWeightUnit() {
        isMetricWeight = !isMetricWeight;
        updateWeightUnitUI();
        editTextWeight.setText("");
    }

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

    private void updateWeightUnitUI() {
        if (isMetricWeight) {
            buttonKg.setText("kg");
            editTextWeight.setHint("Enter weight in kg");
        } else {
            buttonKg.setText("lbs");
            editTextWeight.setHint("Enter weight in lbs");
        }
    }

    private String getFinalHeightInCm() {
        String feetStr = editTextFeet.getText().toString().trim();
        String inchesStr = editTextInches.getText().toString().trim();

        if (feetStr.isEmpty()) return null;

        try {
            if (isMetricHeight) {
                double cm = Double.parseDouble(feetStr);
                return (cm > 0) ? String.valueOf(cm) : null;
            } else {
                double feet = Double.parseDouble(feetStr);
                double inches = inchesStr.isEmpty() ? 0 : Double.parseDouble(inchesStr);
                if (feet <= 0 && inches <= 0) return null;
                double totalCm = (feet * 30.48) + (inches * 2.54);
                return String.valueOf(Math.round(totalCm));
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getFinalWeightInKg() {
        String weightStr = editTextWeight.getText().toString().trim();
        if (weightStr.isEmpty()) return null;

        try {
            double weight = Double.parseDouble(weightStr);
            if (weight <= 0) return null;

            if (isMetricWeight) {
                return String.valueOf(weight);
            } else {
                double totalKg = weight * 0.453592;
                return String.valueOf(Math.round(totalKg * 10.0) / 10.0);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
