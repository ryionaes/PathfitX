package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // FORCIBLY SIGN OUT FOR TESTING
        FirebaseAuth.getInstance().signOut();

        // Clear the onboarding complete flag to ensure a clean slate for new users
        SharedPreferences settings = getSharedPreferences("UserPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("IS_ONBOARDING_COMPLETE");
        editor.apply();

        // Decide where to go next
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            // This block will not be reached during testing because of the signOut() above
            intent = new Intent(SplashActivity.this, HomeScreen.class);
        } else {
            // No user is signed in
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
