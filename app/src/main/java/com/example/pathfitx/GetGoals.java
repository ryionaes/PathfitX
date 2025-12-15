package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetGoals extends AppCompatActivity {

    private static final String TAG = "GetGoalsActivity";
    // Variables
    private Button nextBtn;
    private ImageButton backBtn;
    private TextView setUser;
    private CheckBox chLoseWeight, chGainWeight, chMaintainWeight, chGainMuscle, chStayActive, chImproveFlexibility, chIncreaseEndurance, chBoostEnergyLevel;
    private final List<CheckBox> allGoals = new ArrayList<>();
    private boolean isProgramitacallyChanging = false;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_goals);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        initializeViews();
        populateGoalsList();

        // Set greeting from GetUser activity
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            String greeting = "Hey, " + username + ". 👋 Let‘s start with your goals.";
            setUser.setText(greeting);
        }

        // Button Listeners
        backBtn.setOnClickListener(v -> finish()); // Go back to the previous screen
        nextBtn.setOnClickListener(v -> saveGoalsAndContinue());

        // Checkbox Logic
        setupCheckboxListeners();

        // Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveGoalsAndContinue() {
        List<String> selectedGoals = collectSelectedGoals();

        if (selectedGoals.isEmpty()) {
            Toast.makeText(GetGoals.this, "Please select at least one goal.", Toast.LENGTH_SHORT).show();
            return; // Prevent navigation
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user is currently logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> goalsData = new HashMap<>();
        goalsData.put("goals", selectedGoals);

        db.collection("users").document(currentUser.getUid())
                .update(goalsData) // Use update to add/modify fields without overwriting the doc
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Goals successfully saved!");
                    // Navigate to the next screen (GetInfo)
                    Intent intent = new Intent(GetGoals.this, GetInfo.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(GetGoals.this, "Error saving goals: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void initializeViews() {
        nextBtn = findViewById(R.id.nextButton);
        backBtn = findViewById(R.id.backButton);
        setUser = findViewById(R.id.setUser);
        chLoseWeight = findViewById(R.id.checkLoseWeight);
        chGainWeight = findViewById(R.id.checkGainWeight);
        chMaintainWeight = findViewById(R.id.checkMaintainWeight);
        chGainMuscle = findViewById(R.id.checkGainMuscle);
        chStayActive = findViewById(R.id.checkStayActive);
        chImproveFlexibility = findViewById(R.id.checkImproveFlexibility);
        chIncreaseEndurance = findViewById(R.id.checkIncreaseEndurance);
        chBoostEnergyLevel = findViewById(R.id.checkBoostEnergyLevels);
    }

    private void populateGoalsList() {
        allGoals.add(chLoseWeight);
        allGoals.add(chMaintainWeight);
        allGoals.add(chGainWeight);
        allGoals.add(chGainMuscle);
        allGoals.add(chStayActive);
        allGoals.add(chImproveFlexibility);
        allGoals.add(chIncreaseEndurance);
        allGoals.add(chBoostEnergyLevel);
    }

    private void setupCheckboxListeners() {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            if (isProgramitacallyChanging) return;

            isProgramitacallyChanging = true;
            try {
                // Mutually Exclusive Weight Goals
                if (isChecked) {
                    if (buttonView.getId() == R.id.checkLoseWeight) {
                        chMaintainWeight.setChecked(false);
                        chGainWeight.setChecked(false);
                    } else if (buttonView.getId() == R.id.checkMaintainWeight) {
                        chLoseWeight.setChecked(false);
                        chGainWeight.setChecked(false);
                    } else if (buttonView.getId() == R.id.checkGainWeight) {
                        chLoseWeight.setChecked(false);
                        chMaintainWeight.setChecked(false);
                    }
                }

                // Max 3 Goals
                int count = 0;
                for (CheckBox cb : allGoals) {
                    if (cb.isChecked()) count++;
                }

                if (count > 3) {
                    buttonView.setChecked(false);
                    Toast.makeText(GetGoals.this, "You can only select up to 3 goals.", Toast.LENGTH_SHORT).show();
                }
            } finally {
                isProgramitacallyChanging = false;
            }
        };

        for (CheckBox cb : allGoals) {
            cb.setOnCheckedChangeListener(listener);
        }
    }

    private List<String> collectSelectedGoals() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : allGoals) {
            if (cb.isChecked()) {
                selected.add(cb.getText().toString());
            }
        }
        return selected;
    }
}
