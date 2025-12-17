package com.example.pathfitx;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // --- UI Elements ---
    private TextView tvUserName, tvWeight, tvHeight, tvAge, tvEditProfile, tvChangePassword, tvEditGoals;
    private CardView btnEditProfile, btnNotifications, btnSettings, btnPrivacy;
    private MaterialButton btnLogout;
    private CardView cvProfileImage;
    private View btnCamera;
    private ImageView ivProfile;
    private ImageView ivArrowAccount, ivArrowNotifications, ivArrowHelp, ivArrowAbout;
    private TextView contentAccountInfo;
    private LinearLayout layoutAccountDetails, contentHelp, contentAbout;
    private LinearLayout layoutNotificationSettings;
    private SwitchMaterial switchWaterReminder, switchWorkoutAlerts;
    private TextView tvWaterTime, tvWorkoutTime;
    private ChipGroup chipGroupGoals;

    // --- Image Picker ---
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // --- ViewModel ---
    private SharedViewModel sharedViewModel;

    // --- Time Settings ---
    private int waterHour = 10, waterMinute = 0; // Default start time for water
    private int workoutHour = 8, workoutMinute = 0; // Default time for workout

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeImagePicker();
        
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
                // Revert switch if permission denied
                if (switchWaterReminder != null && switchWaterReminder.isChecked()) switchWaterReminder.setChecked(false);
                if (switchWorkoutAlerts != null && switchWorkoutAlerts.isChecked()) switchWorkoutAlerts.setChecked(false);
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
        initializeViews(view);
        setupListeners();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe for future updates
        sharedViewModel.getUserSnapshot().observe(getViewLifecycleOwner(), this::updateUIWithData);

        // Update UI with current data if it already exists to prevent flicker
        DocumentSnapshot userSnapshot = sharedViewModel.getUserSnapshot().getValue();
        if (userSnapshot != null) {
            updateUIWithData(userSnapshot);
        } else {
            updateUIForGuest();
        }
    }

    private void updateUIWithData(@NonNull DocumentSnapshot snapshot) {
        if (!isAdded() || getContext() == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String username = snapshot.getString("username");
        String email = snapshot.getString("email");
        Double weight = snapshot.getDouble("weight_kg");
        Double height = snapshot.getDouble("height_cm");
        Long age = snapshot.getLong("age");
        List<String> goals = (List<String>) snapshot.get("goals");
        String profileImageUri = snapshot.getString("profileImageUri");

        tvUserName.setText(username != null ? username : "");
        tvWeight.setText(weight != null ? String.valueOf(weight.intValue()) + "kg" : "");
        tvHeight.setText(height != null ? String.valueOf(height.intValue()) + "cm" : "");
        tvAge.setText(age != null ? String.valueOf(age.intValue()) : "");

        String accountInfo = "User ID: " + (currentUser != null ? currentUser.getUid() : "N/A") + "\nEmail: " + (email != null ? email : "N/A");
        contentAccountInfo.setText(accountInfo);
        
        // Update notification switches if fields exist in snapshot, otherwise defaults are used
        if (snapshot.contains("waterReminder")) {
            boolean enabled = Boolean.TRUE.equals(snapshot.getBoolean("waterReminder"));
            switchWaterReminder.setChecked(enabled);
            tvWaterTime.setVisibility(enabled ? View.VISIBLE : View.GONE);
            if (enabled) scheduleWaterReminder(true);
        }
        
        if (snapshot.contains("waterHour") && snapshot.contains("waterMinute")) {
            waterHour = snapshot.getLong("waterHour").intValue();
            waterMinute = snapshot.getLong("waterMinute").intValue();
            updateTimeText(tvWaterTime, waterHour, waterMinute, "Start Time");
        }

        if (snapshot.contains("workoutAlerts")) {
            boolean enabled = Boolean.TRUE.equals(snapshot.getBoolean("workoutAlerts"));
            switchWorkoutAlerts.setChecked(enabled);
            tvWorkoutTime.setVisibility(enabled ? View.VISIBLE : View.GONE);
            if (enabled) scheduleWorkoutAlert(true);
        }

        if (snapshot.contains("workoutHour") && snapshot.contains("workoutMinute")) {
            workoutHour = snapshot.getLong("workoutHour").intValue();
            workoutMinute = snapshot.getLong("workoutMinute").intValue();
            updateTimeText(tvWorkoutTime, workoutHour, workoutMinute, "Time");
        }

        loadProfileImage(profileImageUri);
        displayGoals(goals);
    }

    private void updateTimeText(TextView tv, int hour, int minute, String prefix) {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        tv.setText(prefix + ": " + timeStr);
    }

    private void updateUIForGuest() {
        if (!isAdded() || getContext() == null) return;
        tvUserName.setText("Guest");
        tvWeight.setText("");
        tvHeight.setText("");
        tvAge.setText("");
        contentAccountInfo.setText("");
        chipGroupGoals.removeAllViews();
        loadProfileImage(null);
    }

    private void displayGoals(List<String> goals) {
        if (getContext() == null) return;
        chipGroupGoals.removeAllViews();
        if (goals == null || goals.isEmpty()) {
            return;
        }

        for (String goal : goals) {
            Chip chip = new Chip(getContext());
            chip.setText(goal);
            chipGroupGoals.addView(chip);
        }
    }

    private void showEditProfileDialog() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getContext() == null || currentUser == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etWeight = dialogView.findViewById(R.id.etEditWeight);
        EditText etHeight = dialogView.findViewById(R.id.etEditHeight);
        EditText etAge = dialogView.findViewById(R.id.etEditAge);
        Button btnSave = dialogView.findViewById(R.id.btnSaveProfile);

        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).get().addOnSuccessListener(snapshot -> {
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

                FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
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
        layoutNotificationSettings = view.findViewById(R.id.layoutNotificationSettings);
        switchWaterReminder = view.findViewById(R.id.switchWaterReminder);
        switchWorkoutAlerts = view.findViewById(R.id.switchWorkoutAlerts);
        contentHelp = view.findViewById(R.id.contentHelp);
        contentAbout = view.findViewById(R.id.contentAbout);
        chipGroupGoals = view.findViewById(R.id.chipGroupGoals);
        tvEditGoals = view.findViewById(R.id.tvEditGoals);
        tvWaterTime = view.findViewById(R.id.tvWaterTime);
        tvWorkoutTime = view.findViewById(R.id.tvWorkoutTime);
    }

    private void setupListeners() {
        btnCamera.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        tvEditProfile.setOnClickListener(v -> showEditProfileDialog());

        btnEditProfile.setOnClickListener(v -> toggleVisibility(layoutAccountDetails, ivArrowAccount));
        btnNotifications.setOnClickListener(v -> toggleVisibility(layoutNotificationSettings, ivArrowNotifications));
        btnSettings.setOnClickListener(v -> toggleVisibility(contentHelp, ivArrowHelp));
        btnPrivacy.setOnClickListener(v -> toggleVisibility(contentAbout, ivArrowAbout));

        tvChangePassword.setOnClickListener(v -> sendPasswordResetEmail());
        
        switchWaterReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkNotificationPermission();
            tvWaterTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            saveNotificationPreference("waterReminder", isChecked);
            scheduleWaterReminder(isChecked);
        });

        switchWorkoutAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) checkNotificationPermission();
            tvWorkoutTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            saveNotificationPreference("workoutAlerts", isChecked);
            scheduleWorkoutAlert(isChecked);
        });

        tvWaterTime.setOnClickListener(v -> showTimePicker(true));
        tvWorkoutTime.setOnClickListener(v -> showTimePicker(false));

        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());

        tvEditGoals.setOnClickListener(v -> {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), EditGoals.class));
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Log Out", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showTimePicker(boolean isWater) {
        if (getContext() == null) return;
        int hour = isWater ? waterHour : workoutHour;
        int minute = isWater ? waterMinute : workoutMinute;

        TimePickerDialog picker = new TimePickerDialog(getContext(), (view, h, m) -> {
            if (isWater) {
                waterHour = h;
                waterMinute = m;
                updateTimeText(tvWaterTime, h, m, "Start Time");
                saveTimePreference("waterHour", h, "waterMinute", m);
                if (switchWaterReminder.isChecked()) scheduleWaterReminder(true);
            } else {
                workoutHour = h;
                workoutMinute = m;
                updateTimeText(tvWorkoutTime, h, m, "Time");
                saveTimePreference("workoutHour", h, "workoutMinute", m);
                if (switchWorkoutAlerts.isChecked()) scheduleWorkoutAlert(true);
            }
        }, hour, minute, true); // true for 24h format
        picker.show();
    }

    private void saveTimePreference(String keyHour, int h, String keyMinute, int m) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put(keyHour, h);
        data.put(keyMinute, m);
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(data);
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void scheduleWaterReminder(boolean enable) {
        if (getContext() == null) return;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_WATER_REMINDER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 101, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (enable) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, waterHour);
            calendar.set(Calendar.MINUTE, waterMinute);
            calendar.set(Calendar.SECOND, 0);

            long now = System.currentTimeMillis();
            long triggerTime = calendar.getTimeInMillis();

            if (triggerTime < now) {
                triggerTime += 24 * 60 * 60 * 1000;
            }
            
            if (calendar.getTimeInMillis() < now) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            long interval = 2 * 60 * 60 * 1000; 

            if (alarmManager != null) {
                 try {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
                } catch (SecurityException e) {
                    Log.e(TAG, "Error scheduling alarm: " + e.getMessage());
                }
            }
        } else {
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private void scheduleWorkoutAlert(boolean enable) {
        if (getContext() == null) return;
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_WORKOUT_ALERT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 102, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (enable) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, workoutHour);
            calendar.set(Calendar.MINUTE, workoutMinute);
            calendar.set(Calendar.SECOND, 0);
            
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (alarmManager != null) {
                try {
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                } catch (SecurityException e) {
                    Log.e(TAG, "Error scheduling alarm: " + e.getMessage());
                }
            }
        } else {
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private void saveNotificationPreference(String key, boolean isEnabled) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        
        Map<String, Object> data = new HashMap<>();
        data.put(key, isEnabled);
        
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(data)
                .addOnFailureListener(e -> {
                     if (getContext() != null) {
                         Toast.makeText(getContext(), "Failed to update preference", Toast.LENGTH_SHORT).show();
                     }
                });
    }

    private void sendPasswordResetEmail() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(task -> {
                        if (getContext() != null) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else if (getContext() != null) {
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
                if (error != null && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveProfileImageUriToFirestore(String uriString) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        Map<String, Object> data = new HashMap<>();
        data.put("profileImageUri", uriString);
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(data)
                .addOnSuccessListener(aVoid -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to save image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadProfileImage(String uriString) {
        if (getContext() == null || ivProfile == null) return;

        if (uriString != null && !uriString.isEmpty()) {
            Glide.with(this).load(Uri.parse(uriString)).circleCrop().into(ivProfile);
        } else {
            Glide.with(this).load(R.drawable.ic_profile_default).circleCrop().into(ivProfile);
        }
    }

    private void toggleVisibility(View view, ImageView arrow) {
        if (view.getParent() instanceof ViewGroup) {
            android.transition.TransitionManager.beginDelayedTransition((ViewGroup) view.getParent());
        }
        boolean isVisible = view.getVisibility() == View.VISIBLE;
        view.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        arrow.setRotation(isVisible ? -90 : 0);
    }
}
