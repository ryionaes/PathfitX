package com.example.pathfitx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// 1. NEW IMPORTS FOR GOOGLE SIGN IN
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnGoogle;
    private ImageButton btnBack;
    private TextView tvLogin;

    // 2. GOOGLE SIGN IN VARIABLES
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 3. SETUP GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnBack = findViewById(R.id.btnBack);
        tvLogin = findViewById(R.id.tvLogin);

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Register Button
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (validateInput(email, password, confirmPass)) {
                registerUser(email, password);
            }
        });

        // 4. GOOGLE BUTTON (Real Logic)
        btnGoogle.setOnClickListener(v -> {
            signInWithGoogle();
        });

        // Login Link
        tvLogin.setOnClickListener(v -> finish());
    }

    // --- GOOGLE SIGN IN METHODS ---

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            String googleEmail = account.getEmail();
            Toast.makeText(this, "Welcome " + googleEmail, Toast.LENGTH_SHORT).show();

            // Save Data
            SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("EMAIL", googleEmail);
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            // Navigate to next screen (GetUser or MainActivity)
            // Note: If you haven't created 'GetUser.class' yet, change this to 'MainActivity.class'
            Intent intent = new Intent(RegisterActivity.this, GetUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Code 10 = Developer Error (SHA-1 missing in Firebase Console)
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign In Failed. Code: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
        }
    }

    // --- STANDARD REGISTER LOGIC ---

    private boolean validateInput(String email, String password, String confirmPass) {
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); return false; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password is required"); return false; }
        if (password.length() < 6) { etPassword.setError("Password must be at least 6 characters"); return false; }
        if (!password.equals(confirmPass)) { etConfirmPassword.setError("Passwords do not match"); return false; }
        return true;
    }

    private void registerUser(String email, String password) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("EMAIL", email);
        editor.putString("PASSWORD", password);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();

        Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(RegisterActivity.this, GetUser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}