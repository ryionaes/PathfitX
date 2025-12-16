package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etPassword, etConfirmPassword;
    private Button btnContinue;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        mAuth = FirebaseAuth.getInstance();

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (validateInput(password, confirmPass)) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    user.updatePassword(password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SetPasswordActivity.this, "Password set successfully!", Toast.LENGTH_SHORT).show();
                                    // Now that password is set, continue to the main onboarding flow
                                    Intent intent = new Intent(SetPasswordActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(SetPasswordActivity.this, "Failed to set password.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private boolean validateInput(String password, String confirmPass) {
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
}
