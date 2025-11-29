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

public class activity_get_goals extends AppCompatActivity {

    // Variables
    Button nextBtn;
    ImageButton backBtn;
    TextView setUser;
    CheckBox chLoseWeight, chGainWeight, chMaintainWeight, chGainMuscle, chStayActive, chImproveFlexibility, chIncreaseEndurance, chBoostEnergyLevel;
    private List<CheckBox> allGoals = new ArrayList<>(); // A list holding all checkboxes
    boolean isProgramitacallyChanging = false;


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

        //Getting the user input from previous screen
        String name = getIntent().getStringExtra("USERNAME");
        String greeting = "Hey, " + name + ". 👋 Let‘s start with your goals.";
        setUser.setText(greeting);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to getUser
                Intent intent = new Intent(activity_get_goals.this, activity_get_user.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate  to getInfo
                Intent intent = new Intent(activity_get_goals.this, activity_get_info.class);
                startActivity(intent);
            }
        });

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isProgramitacallyChanging) {
                    return;
                }

                isProgramitacallyChanging = true;

                try {
                    // LOGIC 1 > If the user checked one of the weight goals, uncheck the others.
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

                    int count = 0;
                    for (CheckBox cb : allGoals) {
                        if (cb.isChecked()) {
                            count++;
                        }
                    }

                    if (count > 3) {
                        buttonView.setChecked(false);
                        Toast.makeText(activity_get_goals.this,
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
}