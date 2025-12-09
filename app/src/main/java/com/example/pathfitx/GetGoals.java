package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class GetGoals extends AppCompatActivity {

    // Variables
    Button nextBtn;
    ImageButton backBtn;
    TextView setUser;
    CheckBox chLoseWeight, chGainWeight, chMaintainWeight, chGainMuscle, chStayActive, chImproveFlexibility, chIncreaseEndurance, chBoostEnergyLevel;
    private List<CheckBox> allGoals = new ArrayList<>();
    boolean isProgramitacallyChanging = false;
    private String username; // Variable to store the username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_goals);

        // Initialize view
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

        // Adding them to list
        allGoals.add(chLoseWeight);
        allGoals.add(chMaintainWeight);
        allGoals.add(chGainWeight);
        allGoals.add(chGainMuscle);
        allGoals.add(chStayActive);
        allGoals.add(chImproveFlexibility);
        allGoals.add(chIncreaseEndurance);
        allGoals.add(chBoostEnergyLevel);

        // Getting the user input from previous screen and storing it
        username = getIntent().getStringExtra("USERNAME");
        String greeting = "Hey, " + username + ". 👋 Let‘s start with your goals.";
        setUser.setText(greeting);


        // --- Back Button Logic ---
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to GetUser
                Intent intent = new Intent(GetGoals.this, GetUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // --- NEXT Button Logic (Data Collection and Navigation) ---
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedGoals = collectSelectedGoals();

                if (selectedGoals.isEmpty()) {
                    Toast.makeText(GetGoals.this, "Please select at least one goal.", Toast.LENGTH_SHORT).show();
                    return; // Prevent navigation if no goals are selected
                }

                // Navigate to the next screen (GetInfo)
                Intent intent = new Intent(GetGoals.this, GetInfo.class);

                // Pass the essential data to the next activity
                intent.putExtra("USERNAME", username);

                // Pass the list of selected goals
                intent.putStringArrayListExtra("SELECTED_GOALS", (ArrayList<String>) selectedGoals);

                startActivity(intent);
            }
        });

        // --- Checkbox Change Listener (Goal Limit and Mutually Exclusive Logic) ---
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isProgramitacallyChanging) {
                    return;
                }

                isProgramitacallyChanging = true;

                try {
                    // LOGIC 1: Mutually Exclusive Weight Goals
                    if (isChecked) {
                        if (buttonView == chLoseWeight) {
                            chMaintainWeight.setChecked(false);
                            chGainWeight.setChecked(false);
                        } else  if (buttonView == chMaintainWeight) {
                            chLoseWeight.setChecked(false);
                            chGainWeight.setChecked(false);
                        } else if (buttonView == chGainWeight) {
                            chLoseWeight.setChecked(false);
                            chMaintainWeight.setChecked(false);
                        }
                    }

                    // LOGIC 2: Maximum of 3 Goals
                    int count = 0;
                    for (CheckBox cb : allGoals) {
                        if (cb.isChecked()) {
                            count++;
                        }
                    }

                    if (count > 3) {
                        buttonView.setChecked(false);
                        Toast.makeText(GetGoals.this,
                                "You can only select up to 3 goals.",
                                Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    isProgramitacallyChanging = false;
                }
            }
        };

        for (CheckBox cb : allGoals) {
            cb.setOnCheckedChangeListener(listener);
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Helper method to iterate through all checkboxes and return a list of selected goal texts.
     */
    private List<String> collectSelectedGoals() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : allGoals) {
            if (cb.isChecked()) {
                // We use the text property of the checkbox as the goal string
                selected.add(cb.getText().toString());
            }
        }
        return selected;
    }
}
