package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetInfo extends AppCompatActivity {

    private static final String TAG = "GetInfoActivity";
    private EditText ageEditText;
    private Spinner locationSpinner;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_info);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        ageEditText = findViewById(R.id.ageEditText);
        locationSpinner = findViewById(R.id.country_spinner);
        ImageButton backBtn = findViewById(R.id.backButton);
        Button nextBtn = findViewById(R.id.nextButton);

        nextBtn.setOnClickListener(v -> saveInfoAndContinue());

        backBtn.setOnClickListener(v -> finish()); // Simply go back

        setupLocationSpinner();
    }

    private void saveInfoAndContinue() {
        String ageStr = ageEditText.getText().toString().trim();
        String location = locationSpinner.getSelectedItem().toString();

        if (ageStr.isEmpty()) {
            ageEditText.setError("Please enter your age");
            return;
        }

        if (location.equals(getResources().getStringArray(R.array.location_array)[0]))) {
            Toast.makeText(this, "Please select your location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user is currently logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            ageEditText.setError("Please enter a valid age");
            return;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("age", age);
        userInfo.put("location", location);

        db.collection("users").document(currentUser.getUid())
                .update(userInfo)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User info successfully saved!");
                    Intent intent = new Intent(GetInfo.this, GetHeight.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(GetInfo.this, "Error saving info: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupLocationSpinner() {
        String[] locationData = getResources().getStringArray(R.array.location_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_black_text,
                locationData
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black_text);
        locationSpinner.setAdapter(adapter);
        locationSpinner.setSelection(0); // Set default prompt
    }
}
