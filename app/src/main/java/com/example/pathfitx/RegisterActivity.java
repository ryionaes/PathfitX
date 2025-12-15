package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private ImageButton btnBack;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack = findViewById(R.id.btnBack);
        tvLogin = findViewById(R.id.tvLogin);

        // 2. Back Button logic
        btnBack.setOnClickListener(v -> finish()); // Just closes this screen

        // 3. Register Button Logic
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (validateInput(email, password, confirmPass)) {
                registerUser(email, password);
            }
        });

        // 4. "Already have an account? Login" logic
        tvLogin.setOnClickListener(v -> finish());
    }

    private boolean validateInput(String email, String password, String confirmPass) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPass)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password) {
        // Save user data locally
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("REGISTERED_EMAIL", email);
        editor.putString("REGISTERED_PASSWORD", password);
        editor.putBoolean("isLoggedIn", true); // Auto-login after sign up
        editor.apply();

        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

        // Go straight to the App (MainActivity)
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}