package com.example.pathfitx;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditGoals extends AppCompatActivity {

    private static final String TAG = "EditGoalsActivity";
    // Variables
    private Button confirmBtn;
    private ImageButton backBtn;
    private CheckBox chLoseWeight, chGainWeight, chMaintainWeight, chGainMuscle, chStayActive, chImproveFlexibility, chIncreaseEndurance, chBoostEnergyLevel;
    private TextView txtLoseWeight, txtMaintainWeight, txtGainWeight, txtGainMuscle, txtStayActive, txtImproveFlexibility, txtIncreaseEndurance, txtBoostEnergyLevel;
    private final List<CheckBox> allGoals = new ArrayList<>();
    private boolean isProgrammaticallyChanging = false;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_goals);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        initializeViews();
        populateGoalsList();

        loadUserGoals();

        // Button Listeners
        backBtn.setOnClickListener(v -> finish()); // Go back to the previous screen
        confirmBtn.setOnClickListener(v -> saveGoalsAndContinue());

        // Checkbox Logic
        setupCheckboxListeners();

        // Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserGoals() {
        if (currentUser == null) return;
        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> userGoals = (List<String>) documentSnapshot.get("goals");
                if (userGoals != null) {
                    for (String goal : userGoals) {
                        for (CheckBox cb : allGoals) {
                            if (getGoalText(cb).equalsIgnoreCase(goal)) {
                                cb.setChecked(true);
                            }
                        }
                    }
                }
            }
        });
    }

    private void saveGoalsAndContinue() {
        List<String> selectedGoals = collectSelectedGoals();

        if (selectedGoals.isEmpty()) {
            Toast.makeText(EditGoals.this, "Please select at least one goal.", Toast.LENGTH_SHORT).show();
            return; // Prevent navigation
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user is currently logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> goalsData = new HashMap<>();
        goalsData.put("goals", selectedGoals);

        db.collection("users").document(currentUser.getUid())
                .update(goalsData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Goals successfully updated!");
                    Toast.makeText(EditGoals.this, "Goals Applied", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the profile screen
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating document", e);
                    Toast.makeText(EditGoals.this, "Error saving goals: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void initializeViews() {
        confirmBtn = findViewById(R.id.nextButton);
        backBtn = findViewById(R.id.backButton);
        chLoseWeight = findViewById(R.id.checkLoseWeight);
        chGainWeight = findViewById(R.id.checkGainWeight);
        chMaintainWeight = findViewById(R.id.checkMaintainWeight);
        chGainMuscle = findViewById(R.id.checkGainMuscle);
        chStayActive = findViewById(R.id.checkStayActive);
        chImproveFlexibility = findViewById(R.id.checkImproveFlexibility);
        chIncreaseEndurance = findViewById(R.id.checkIncreaseEndurance);
        chBoostEnergyLevel = findViewById(R.id.checkBoostEnergyLevels);

        txtLoseWeight = findViewById(R.id.txtLoseWeight);
        txtMaintainWeight = findViewById(R.id.txtMaintainWeight);
        txtGainWeight = findViewById(R.id.txtGainWeight);
        txtGainMuscle = findViewById(R.id.txtGainMuscle);
        txtStayActive = findViewById(R.id.txtStayActive);
        txtImproveFlexibility = findViewById(R.id.txtImproveFlexibility);
        txtIncreaseEndurance = findViewById(R.id.txtIncreaseEndurance);
        txtBoostEnergyLevel = findViewById(R.id.txtBoostEnergyLevels);
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
            if (isProgrammaticallyChanging) return;

            isProgrammaticallyChanging = true;
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
                    Toast.makeText(EditGoals.this, "You can only select up to 3 goals.", Toast.LENGTH_SHORT).show();
                }
            } finally {
                isProgrammaticallyChanging = false;
            }
        };

        for (CheckBox cb : allGoals) {
            cb.setOnCheckedChangeListener(listener);
        }
    }

    private String getGoalText(CheckBox cb) {
        if (cb.getId() == R.id.checkLoseWeight) return txtLoseWeight.getText().toString();
        if (cb.getId() == R.id.checkMaintainWeight) return txtMaintainWeight.getText().toString();
        if (cb.getId() == R.id.checkGainWeight) return txtGainWeight.getText().toString();
        if (cb.getId() == R.id.checkGainMuscle) return txtGainMuscle.getText().toString();
        if (cb.getId() == R.id.checkStayActive) return txtStayActive.getText().toString();
        if (cb.getId() == R.id.checkImproveFlexibility) return txtImproveFlexibility.getText().toString();
        if (cb.getId() == R.id.checkIncreaseEndurance) return txtIncreaseEndurance.getText().toString();
        if (cb.getId() == R.id.checkBoostEnergyLevels) return txtBoostEnergyLevel.getText().toString();
        return "";
    }

    private List<String> collectSelectedGoals() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : allGoals) {
            if (cb.isChecked()) {
                selected.add(getGoalText(cb));
            }
        }
        return selected;
    }
}
