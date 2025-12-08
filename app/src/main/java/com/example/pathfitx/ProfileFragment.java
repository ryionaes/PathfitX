package com.example.pathfitx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent; // Import for Navigation
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Set;

public class ProfileFragment extends Fragment {

    // --- UI Elements ---
    private TextView tvUserName, tvWeight, tvHeight, tvAge;
    private AppCompatButton btnEditProfile, btnNotifications, btnSettings, btnPrivacy, btnLogout;

    // Image Elements
    private FrameLayout layoutProfileImageContainer;
    private CardView cvProfileImage;
    private ImageView ivProfile;

    // --- Content Views ---
    private TextView contentAccountInfo, contentHelp, contentAbout;
    private LinearLayout layoutAccountDetails;
    private Button btnOpenEditDialog;

    // --- Notification Views ---
    private LinearLayout layoutNotificationSettings;
    private SwitchMaterial switchWaterReminder, switchWorkoutAlerts;

    // --- Image Picker ---
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    // Preference File Names (To clear on logout)
    private static final String USER_PREFS = "UserPrefs";
    private static final String WORKOUT_PREFS = "WorkoutPrefs";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Photo Picker Logic
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                String uriString = uri.toString();
                saveProfileImageUri(uriString);
                loadProfileImage(uriString);
                Toast.makeText(getContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);

        layoutProfileImageContainer = view.findViewById(R.id.layoutProfileImageContainer);
        cvProfileImage = view.findViewById(R.id.cvProfileImage);
        ivProfile = view.findViewById(R.id.ivProfile);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnNotifications = view.findViewById(R.id.btnNotifications);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPrivacy = view.findViewById(R.id.btnPrivacy);
        btnLogout = view.findViewById(R.id.btnLogout);

        layoutAccountDetails = view.findViewById(R.id.layoutAccountDetails);
        contentAccountInfo = view.findViewById(R.id.contentAccountInfo);
        btnOpenEditDialog = view.findViewById(R.id.btnOpenEditDialog);

        layoutNotificationSettings = view.findViewById(R.id.layoutNotificationSettings);
        switchWaterReminder = view.findViewById(R.id.switchWaterReminder);
        switchWorkoutAlerts = view.findViewById(R.id.switchWorkoutAlerts);

        contentHelp = view.findViewById(R.id.contentHelp);
        contentAbout = view.findViewById(R.id.contentAbout);

        loadUserProfile();
        setupListeners();
    }

    private void setupListeners() {
        // Image Click
        View.OnClickListener imageClickListener = v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        };
        if (layoutProfileImageContainer != null) layoutProfileImageContainer.setOnClickListener(imageClickListener);
        if (cvProfileImage != null) cvProfileImage.setOnClickListener(imageClickListener);

        // Account Info Toggle
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                contentAccountInfo.setText(getFullUserDetails());
                toggleVisibility(layoutAccountDetails);
            });
        }

        // Edit Dialog
        if (btnOpenEditDialog != null) {
            btnOpenEditDialog.setOnClickListener(v -> showEditProfileDialog());
        }

        // Other Toggles
        if (btnNotifications != null) btnNotifications.setOnClickListener(v -> toggleVisibility(layoutNotificationSettings));
        if (btnSettings != null) btnSettings.setOnClickListener(v -> toggleVisibility(contentHelp));
        if (btnPrivacy != null) btnPrivacy.setOnClickListener(v -> toggleVisibility(contentAbout));

        // Switches
        if (switchWaterReminder != null) {
            switchWaterReminder.setOnCheckedChangeListener((buttonView, isChecked) -> saveNotificationPreference("NOTIF_WATER", isChecked));
        }
        if (switchWorkoutAlerts != null) {
            switchWorkoutAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> saveNotificationPreference("NOTIF_WORKOUT", isChecked));
        }

        // --- LOG OUT BUTTON LOGIC ---
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                if (getContext() == null) return;

                // 1. Clear All Saved Data
                SharedPreferences userPrefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences workoutPrefs = getContext().getSharedPreferences(WORKOUT_PREFS, Context.MODE_PRIVATE);

                userPrefs.edit().clear().commit(); // Clear Profile Data
                workoutPrefs.edit().clear().commit(); // Clear Home Data

                // 2. Navigate to Welcome Page
                Intent intent = new Intent(getActivity(), WelcomePage.class);

                // 3. Clear the Back Stack (Cannot go back to Profile)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                // 4. Show Toast
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showEditProfileDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etWeight = dialogView.findViewById(R.id.etEditWeight);
        EditText etHeight = dialogView.findViewById(R.id.etEditHeight);
        EditText etAge = dialogView.findViewById(R.id.etEditAge);
        Button btnSave = dialogView.findViewById(R.id.btnSaveProfile);

        SharedPreferences prefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        etName.setText(prefs.getString("USERNAME", ""));
        etWeight.setText(prefs.getString("WEIGHT_KG", ""));
        etHeight.setText(prefs.getString("HEIGHT_CM", ""));
        etAge.setText(prefs.getString("AGE", ""));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("USERNAME", etName.getText().toString().trim());
            editor.putString("WEIGHT_KG", etWeight.getText().toString().trim());
            editor.putString("HEIGHT_CM", etHeight.getText().toString().trim());
            editor.putString("AGE", etAge.getText().toString().trim());

            boolean success = editor.commit();

            if (success) {
                loadUserProfile();
                contentAccountInfo.setText(getFullUserDetails());
                Toast.makeText(getContext(), "Profile Saved!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    private void loadUserProfile() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);

        String name = prefs.getString("USERNAME", "User");
        if (tvUserName != null) tvUserName.setText(name);
        if (tvWeight != null) tvWeight.setText(prefs.getString("WEIGHT_KG", "0") + "kg");
        if (tvHeight != null) tvHeight.setText(prefs.getString("HEIGHT_CM", "0") + "cm");
        if (tvAge != null) tvAge.setText(prefs.getString("AGE", "0"));

        if (switchWaterReminder != null) switchWaterReminder.setChecked(prefs.getBoolean("NOTIF_WATER", true));
        if (switchWorkoutAlerts != null) switchWorkoutAlerts.setChecked(prefs.getBoolean("NOTIF_WORKOUT", false));

        String savedUri = prefs.getString("PROFILE_IMAGE_URI", null);
        loadProfileImage(savedUri);
    }

    private void loadProfileImage(String uriString) {
        if (getContext() == null || ivProfile == null) return;

        if (uriString != null && !uriString.isEmpty()) {
            Glide.with(this)
                    .load(Uri.parse(uriString))
                    .circleCrop()
                    .signature(new ObjectKey(System.currentTimeMillis()))
                    .into(ivProfile);
        } else {
            Glide.with(this)
                    .load(android.R.drawable.sym_def_app_icon)
                    .circleCrop()
                    .into(ivProfile);
        }
    }

    private void saveProfileImageUri(String uriString) {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString("PROFILE_IMAGE_URI", uriString).apply();
        try {
            getContext().getContentResolver().takePersistableUriPermission(
                    Uri.parse(uriString),
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleVisibility(View view) {
        if (view != null) {
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    private void saveNotificationPreference(String key, boolean isChecked) {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, isChecked).apply();
    }

    private String getFullUserDetails() {
        if (getContext() == null) return "No Data";
        SharedPreferences prefs = getContext().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        Set<String> goals = prefs.getStringSet("GOALS", null);
        String goalsStr = (goals != null) ? String.join(", ", goals) : "None";
        return "Name: " + prefs.getString("USERNAME", "User") +
                "\nWeight: " + prefs.getString("WEIGHT_KG", "0") + "kg" +
                "\nHeight: " + prefs.getString("HEIGHT_CM", "0") + "cm" +
                "\nAge: " + prefs.getString("AGE", "0") +
                "\nGoals: " + goalsStr;
    }
}