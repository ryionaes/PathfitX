package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogle;
    private TextView tvSignUp, tvForgotPassword;

    // This helps us save if the user is logged in
    private static final String USER_PREFS = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Check if user is ALREADY logged in. If yes, skip to Main Activity.
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        // 2. Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 3. Login Button Click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                performLogin(email);
            }
        });

        // 4. Google Button Click
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google Login Clicked", Toast.LENGTH_SHORT).show();
        });

        // 5. Sign Up Link Click
        tvSignUp.setOnClickListener(v -> {
            Toast.makeText(this, "Go to Sign Up Screen", Toast.LENGTH_SHORT).show();
            // You can add the intent to move to RegisterActivity here later
        });

        // 6. Forgot Password Link Click
        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        return true;
    }

    private void performLogin(String email) {
        // Save that the user has logged in so they don't have to do it again next time
        SharedPreferences prefs = getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString("EMAIL", email);
        editor.apply();

        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
        goToMainActivity();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Closes Login Activity so you can't go back to it
    }
}