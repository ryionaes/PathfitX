package com.example.pathfitx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnGoogle;
    private ImageButton btnBack;
    private TextView tvLogin;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnBack = findViewById(R.id.btnBack);
        tvLogin = findViewById(R.id.tvLogin);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (validateInput(email, password, confirmPass)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // New user created with email, go to the onboarding flow
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, "An account with this email already exists. Please log in.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    String errorMessage = "Registration failed.";
                                    if (task.getException() != null) {
                                        errorMessage = task.getException().getMessage();
                                        Log.e("RegistrationError", "Registration failed: " + errorMessage);
                                    }
                                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        tvLogin.setOnClickListener(v -> finish());
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Google Sign-In Failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserStatus(task.getResult());
                    } else {
                        Toast.makeText(this, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserStatus(AuthResult authResult) {
        boolean isNewUser = authResult.getAdditionalUserInfo().isNewUser();
        
        if (isNewUser) {
            // Brand new user via Google, prompt to set a password.
            Log.d("RegisterActivity", "Firebase Auth reports a new user. Navigating to SetPasswordActivity.");
            startActivity(new Intent(RegisterActivity.this, SetPasswordActivity.class));
            finish();
        } else {
            // User's Google account is already linked to a Firebase user.
            // This is an existing user.
            Log.d("RegisterActivity", "User already exists. Prompting to log in.");
            Toast.makeText(this, "An account with this Google profile already exists. Please log in.", Toast.LENGTH_LONG).show();
            // Sign the user out to prevent a stuck login state.
            mAuth.signOut();
        }
    }

    private boolean validateInput(String email, String password, String confirmPass) {
        if (TextUtils.isEmpty(email)) { etEmail.setError("Email is required"); return false; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password is required"); return false; }
        if (password.length() < 6) { etPassword.setError("Password must be at least 6 characters"); return false; }
        if (!password.equals(confirmPass)) { etConfirmPassword.setError("Passwords do not match"); return false; }
        return true;
    }
}
