package com.example.pathfitx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // --- UI Elements ---
    private TextView tvUserName, tvWeight, tvHeight, tvAge, tvEditProfile, tvChangePassword;
    private CardView btnEditProfile, btnNotifications, btnSettings, btnPrivacy;
    private MaterialButton btnLogout;
    private CardView cvProfileImage;
    private View btnCamera;
    private ImageView ivProfile;
    private ImageView ivArrowAccount, ivArrowNotifications, ivArrowHelp, ivArrowAbout;
    private TextView contentAccountInfo;
    private LinearLayout layoutAccountDetails, contentHelp, contentAbout;
    private LinearLayout layoutNotificationSettings;
    private Button btnOpenEditDialog;
    private SwitchMaterial switchWaterReminder, switchWorkoutAlerts;

    // --- Image Picker ---
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initializeImagePicker();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupListeners();
        loadUserProfile();
    }

    private void loadUserProfile() {
        if (currentUser == null || getContext() == null) {
            tvUserName.setText("Guest");
            return;
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        Double weight = documentSnapshot.getDouble("weight_kg");
                        Double height = documentSnapshot.getDouble("height_cm");
                        Long age = documentSnapshot.getLong("age");

                        tvUserName.setText(username != null ? username : "N/A");
                        tvWeight.setText(String.format("%s kg", weight != null ? String.valueOf(weight.intValue()) : "0"));
                        tvHeight.setText(String.format("%s cm", height != null ? String.valueOf(height.intValue()) : "0"));
                        tvAge.setText(age != null ? String.valueOf(age.intValue()) : "0");

                        String accountInfo = "User ID: " + currentUser.getUid() + "\nEmail: " + (email != null ? email : "N/A");
                        contentAccountInfo.setText(accountInfo);

                        String profileImageUri = documentSnapshot.getString("profileImageUri");
                        loadProfileImage(profileImageUri);

                    } else {
                        Toast.makeText(getContext(), "User data not found. Please complete profile.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user profile", e);
                    Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showEditProfileDialog() {
        if (getContext() == null || currentUser == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etWeight = dialogView.findViewById(R.id.etEditWeight);
        EditText etHeight = dialogView.findViewById(R.id.etEditHeight);
        EditText etAge = dialogView.findViewById(R.id.etEditAge);
        Button btnSave = dialogView.findViewById(R.id.btnSaveProfile);

        // Pre-fill dialog with current data
        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etName.setText(snapshot.getString("username"));
                etWeight.setText(String.valueOf(snapshot.getDouble("weight_kg")));
                etHeight.setText(String.valueOf(snapshot.getDouble("height_cm")));
                etAge.setText(String.valueOf(snapshot.getLong("age")));
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newWeightStr = etWeight.getText().toString().trim();
            String newHeightStr = etHeight.getText().toString().trim();
            String newAgeStr = etAge.getText().toString().trim();

            if (newName.isEmpty() || newWeightStr.isEmpty() || newHeightStr.isEmpty() || newAgeStr.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("username", newName);
                updatedData.put("weight_kg", Double.parseDouble(newWeightStr));
                updatedData.put("height_cm", Double.parseDouble(newHeightStr));
                updatedData.put("age", Long.parseLong(newAgeStr));

                db.collection("users").document(currentUser.getUid()).update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                            loadUserProfile(); // Refresh the profile display
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void initializeViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);
        tvEditProfile = view.findViewById(R.id.tvEditProfile);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
        cvProfileImage = view.findViewById(R.id.cvProfileImage);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnCamera = view.findViewById(R.id.btnCamera);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNotifications = view.findViewById(R.id.btnNotifications);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPrivacy = view.findViewById(R.id.btnPrivacy);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivArrowAccount = view.findViewById(R.id.ivArrowAccount);
        ivArrowNotifications = view.findViewById(R.id.ivArrowNotifications);
        ivArrowHelp = view.findViewById(R.id.ivArrowHelp);
        ivArrowAbout = view.findViewById(R.id.ivArrowAbout);
        layoutAccountDetails = view.findViewById(R.id.layoutAccountDetails);
        contentAccountInfo = view.findViewById(R.id.contentAccountInfo);
        btnOpenEditDialog = view.findViewById(R.id.btnOpenEditDialog);
        layoutNotificationSettings = view.findViewById(R.id.layoutNotificationSettings);
        switchWaterReminder = view.findViewById(R.id.switchWaterReminder);
        switchWorkoutAlerts = view.findViewById(R.id.switchWorkoutAlerts);
        contentHelp = view.findViewById(R.id.contentHelp);
        contentAbout = view.findViewById(R.id.contentAbout);
    }

    private void setupListeners() {
        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
        }

        if (tvEditProfile != null) {
            tvEditProfile.setOnClickListener(v -> showEditProfileDialog());
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                toggleVisibility(layoutAccountDetails, ivArrowAccount);
            });
        }

        if (btnOpenEditDialog != null) {
            btnOpenEditDialog.setOnClickListener(v -> showEditProfileDialog());
        }

        if (tvChangePassword != null) {
            tvChangePassword.setOnClickListener(v -> sendPasswordResetEmail());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                if (getContext() == null) return;
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void sendPasswordResetEmail() {
        if (currentUser != null && currentUser.getEmail() != null) {
            mAuth.sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Could not get user email.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeImagePicker() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions())
                        .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setCropShape(CropImageView.CropShape.OVAL);
                cropImage.launch(options);
            }
        });

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                Uri resultUri = result.getUriContent();
                saveProfileImageUriToFirestore(resultUri.toString());
            } else {
                Exception error = result.getError();
                if (error != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveProfileImageUriToFirestore(String uriString) {
        if (currentUser == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put("profileImageUri", uriString);
        db.collection("users").document(currentUser.getUid()).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
                    loadProfileImage(uriString);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save image.", Toast.LENGTH_SHORT).show());
    }

    private void loadProfileImage(String uriString) {
        if (getContext() == null || ivProfile == null) return;

        if (uriString != null && !uriString.isEmpty()) {
            Glide.with(this).load(Uri.parse(uriString)).circleCrop()
                    .signature(new ObjectKey(System.currentTimeMillis())).into(ivProfile);
        } else {
            // You should have a default placeholder image in your drawables
            // Glide.with(this).load(R.drawable.ic_profile_default).circleCrop().into(ivProfile);
        }
    }

    private void toggleVisibility(View view, ImageView arrow) {
        boolean isVisible = view.getVisibility() == View.VISIBLE;
        view.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.setRotation(isVisible ? -90 : 0);
    }
}
