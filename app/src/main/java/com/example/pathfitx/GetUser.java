package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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

import java.util.HashMap;
import java.util.Map;

public class GetUser extends AppCompatActivity {

    private static final String TAG = "GetUserActivity";
    private static final String KEY_NAV_SOURCE = "NAV_SOURCE";
    private static final String SOURCE_PROFILE = "PROFILE";

    private ImageButton backButton;
    private Button nextButton;
    private EditText getUser;
    private String navigationSource;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_user);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigationSource = getIntent().getStringExtra(KEY_NAV_SOURCE);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        getUser = findViewById(R.id.inputText);

        if (SOURCE_PROFILE.equals(navigationSource)) {
            nextButton.setText("SAVE");
        }

        backButton.setOnClickListener(v -> handleBackButton());

        nextButton.setOnClickListener(v -> saveUsername());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        });
    }

    private void handleBackButton() {
        if (SOURCE_PROFILE.equals(navigationSource)) {
            finish();
        } else {
            finish();
        }
    }

    private void saveUsername() {
        String username = getUser.getText().toString().trim();

        if (username.isEmpty()) {
            getUser.setError("Username is required");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: No user is currently logged in.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a data map for Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", currentUser.getEmail());

        // Save to Firestore in "users" collection with the user's UID as the document ID
        db.collection("users").document(currentUser.getUid())
                .set(user) // .set() will create or overwrite the document.
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(GetUser.this, "Username saved!", Toast.LENGTH_SHORT).show();

                        if (SOURCE_PROFILE.equals(navigationSource)) {
                            finish(); // Go back to the profile screen
                        } else {
                            // New User Flow: proceed to the next step
                            Intent intent = new Intent(GetUser.this, GetGoals.class);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(GetUser.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
