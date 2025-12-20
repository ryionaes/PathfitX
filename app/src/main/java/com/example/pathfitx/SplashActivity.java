package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String REG_STEP_KEY = "REGISTRATION_STEP";
    private static final String ONBOARDING_COMPLETE_KEY = "IS_ONBOARDING_COMPLETE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            if (currentUser != null) {
                // Check Firestore if user document exists and is complete
                FirebaseFirestore.getInstance().collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // Check if document exists AND contains "weight_kg" (from GetHeight, the final step) AND "goals" (from GetGoals, the second step)
                            if (document != null && document.exists() && document.contains("weight_kg") && document.contains("goals")) {
                                // User has a complete profile, go to Home
                                startActivity(new Intent(SplashActivity.this, HomeScreen.class));
                                finish();
                            } else {
                                // User has no profile or incomplete profile, show confirmation dialog before resuming
                                showResumeDialog(currentUser, prefs);
                            }
                        } else {
                            // On error, fallback to checking local progress or go to login
                            showResumeDialog(currentUser, prefs);
                        }
                    });
            } else {
                // No user logged in, go to Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }

    private void showResumeDialog(FirebaseUser user, SharedPreferences prefs) {
        String email = user.getEmail();
        String message = "A detected sign up attempt was made using " + (email != null ? email : "your account") + ".\n\nWould you like to continue where you left off?";

        new AlertDialog.Builder(this)
                .setTitle("Resume Registration")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Continue", (dialog, which) -> {
                    navigateBasedOnProgress(prefs);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Delete the user account
                    user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("SplashActivity", "User account deleted.");
                                // Remove local progress prefs so they start clean next time
                                prefs.edit().clear().apply();
                                // Also delete the incomplete document if it exists to be clean
                                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).delete();
                            } else {
                                Log.e("SplashActivity", "User account deletion failed.", task.getException());
                                // Even if deletion fails, we still sign out
                                FirebaseAuth.getInstance().signOut();
                            }
                            // Go back to Login
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        });
                })
                .show();
    }

    private void navigateBasedOnProgress(SharedPreferences prefs) {
        String currentStep = prefs.getString(REG_STEP_KEY, "START");
        boolean isOnboardingDone = prefs.getBoolean(ONBOARDING_COMPLETE_KEY, false);

        Intent intent;
        switch (currentStep) {
            case "STEP_USER":
                // Finished User -> Go to Goals
                intent = new Intent(SplashActivity.this, GetGoals.class);
                break;
            case "STEP_GOALS":
                // Finished Goals -> Go to Info
                intent = new Intent(SplashActivity.this, GetInfo.class);
                break;
            case "STEP_INFO":
                // Finished Info -> Go to Height
                intent = new Intent(SplashActivity.this, GetHeight.class);
                break;
            case "STEP_HEIGHT":
                // Finished Height -> Done
                if (isOnboardingDone) {
                    intent = new Intent(SplashActivity.this, HomeScreen.class);
                } else {
                    intent = new Intent(SplashActivity.this, WelcomePage.class);
                }
                break;
            default:
                // Start -> Go to User
                intent = new Intent(SplashActivity.this, GetUser.class);
                break;
        }
        startActivity(intent);
    }
}
