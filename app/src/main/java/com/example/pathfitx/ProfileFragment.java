package com.example.pathfitx;
import com.google.android.material.R.attr;
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
import android.text.TextUtils;
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
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_OPEN_NOTIFICATIONS = "open_notifications";

    // UI
    private TextView tvUserName, tvEditProfile, tvWeight, tvHeight, tvAge, tvChangePassword, tvEditGoals;
    private ImageView ivProfile, ivArrowAccount, ivArrowNotifications, ivArrowHelp, ivArrowAbout;
    private CardView btnCamera;
    private Button btnLogout;
    private TextView contentAccountInfo, tvDateJoined, tvPrimaryLocation;
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

    public static ProfileFragment newInstance(boolean openNotifications) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_OPEN_NOTIFICATIONS, openNotifications);
        fragment.setArguments(args);
        return fragment;
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
                if (switchWaterReminder != null && switchWaterReminder.isChecked()) switchWaterReminder.setChecked(false);
                if (switchWorkoutAlerts != null && switchWorkoutAlerts.isChecked()) switchWorkoutAlerts.setChecked(false);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners(view);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserSnapshot().observe(getViewLifecycleOwner(), this::updateUI);

        if (getArguments() != null && getArguments().getBoolean(ARG_OPEN_NOTIFICATIONS)) {
            view.post(() -> {
                if (layoutNotificationSettings.getVisibility() != View.VISIBLE) {
                    toggleVisibility(layoutNotificationSettings, ivArrowNotifications);
                }
            });
        }
    }

    private void updateUI(DocumentSnapshot snapshot) {
        if (!isAdded() || getContext() == null) return;

        if (snapshot == null || !snapshot.exists()) {
            updateUIForGuest();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String name = snapshot.getString("username");
        String email = snapshot.getString("email");
        String location = snapshot.getString("location");
        String profileImageUri = snapshot.getString("profileImageUri");
        List<String> goals = (List<String>) snapshot.get("goals");

        tvUserName.setText(name != null ? name : "Guest");

        Object weightObj = snapshot.get("weight_kg");
        if (weightObj instanceof Number) {
            tvWeight.setText(String.format(Locale.getDefault(), "%.1fkg", ((Number) weightObj).doubleValue()));
        } else {
            tvWeight.setText("N/A");
        }

        Object heightObj = snapshot.get("height_cm");
        if (heightObj instanceof Number) {
            tvHeight.setText(String.format(Locale.getDefault(), "%.1fcm", ((Number) heightObj).doubleValue()));
        } else {
            tvHeight.setText("N/A");
        }

        Object ageObj = snapshot.get("age");
        if (ageObj instanceof Number) {
            tvAge.setText(String.valueOf(((Number) ageObj).intValue()));
        } else {
            tvAge.setText("N/A");
        }

        String accountInfo = "Email: " + (email != null ? email : "N/A");
        contentAccountInfo.setText(accountInfo);

        if (currentUser != null && currentUser.getMetadata() != null) {
            long creationTimestamp = currentUser.getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvDateJoined.setText("Member since: " + sdf.format(new Date(creationTimestamp)));
        }

        tvPrimaryLocation.setText("Location: " + (location != null ? location : "N/A"));

        // Set switch state based on database, defaulting to false if not present
        boolean waterReminderEnabled = snapshot.contains("waterReminder") ? Boolean.TRUE.equals(snapshot.getBoolean("waterReminder")) : false;
        switchWaterReminder.setChecked(waterReminderEnabled);
        tvWaterTime.setVisibility(waterReminderEnabled ? View.VISIBLE : View.GONE);
        if (waterReminderEnabled) scheduleWaterReminder(true);

        if (snapshot.contains("waterHour") && snapshot.contains("waterMinute")) {
            waterHour = snapshot.getLong("waterHour").intValue();
            waterMinute = snapshot.getLong("waterMinute").intValue();
            updateTimeText(tvWaterTime, waterHour, waterMinute, "Start Time");
        }

        boolean workoutAlertsEnabled = snapshot.contains("workoutAlerts") ? Boolean.TRUE.equals(snapshot.getBoolean("workoutAlerts")) : false;
        switchWorkoutAlerts.setChecked(workoutAlertsEnabled);
        tvWorkoutTime.setVisibility(workoutAlertsEnabled ? View.VISIBLE : View.GONE);
        if (workoutAlertsEnabled) scheduleWorkoutAlert(true);

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
        tvWeight.setText("N/A");
        tvHeight.setText("N/A");
        tvAge.setText("N/A");
        tvEditProfile.setVisibility(View.GONE);
        btnCamera.setVisibility(View.GONE);
        chipGroupGoals.removeAllViews();
        Chip guestChip = new Chip(getContext());
        guestChip.setText("Sign up to set goals");
        chipGroupGoals.addView(guestChip);
        btnLogout.setText("Sign In");
    }

    private void loadProfileImage(String uriString) {
        if (uriString != null && isAdded() && getContext() != null) {
            Glide.with(getContext()).load(Uri.parse(uriString)).placeholder(android.R.drawable.sym_def_app_icon).error(android.R.drawable.sym_def_app_icon).into(ivProfile);
        }
    }

    private void displayGoals(List<String> goals) {
        if (getContext() == null) return;
        chipGroupGoals.removeAllViews();

        if (goals == null || goals.isEmpty()) {
            Chip noGoalsChip = new Chip(getContext());
            noGoalsChip.setText("No goals set");
            chipGroupGoals.addView(noGoalsChip);
            return;
        }

        // Gamit tayo ng ContextThemeWrapper para sa ating GoalChip style
        for (String goal : goals) {
            // Pinalitan ko yung attribute sa R.attr.chipStyle para compatible sa lahat
            Chip chip = new Chip(new ContextThemeWrapper(getContext(), R.style.GoalChip), null, attr.chipStyle);
            chip.setText(goal);

            // Eto ang "Puwersa" method: Kahit anong style ang ilagay ng system,
            // ito ang masusunod na kulay at border para siguradong red at no border
            chip.setChipBackgroundColorResource(R.color.red_bg_light); // Yung light red bg
            chip.setTextColor(ContextCompat.getColor(getContext(), R.color.prim_red)); // Yung dark red text
            chip.setChipStrokeWidth(0f); // Siguradong walang border
            chip.setChipStrokeColorResource(android.R.color.transparent); // Gawing transparent yung stroke

            chipGroupGoals.addView(chip);
        }
    }

    private void initializeImagePicker() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                launchImageCropper(uri);
            } else {
                Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            }
        });
        cropImage = registerForActivityResult(new com.canhub.cropper.CropImageContract(), result -> {
            if (result.isSuccessful()) {
                Uri croppedUri = result.getUriContent();
                ivProfile.setImageURI(croppedUri);
                uploadProfileImage(croppedUri);
            } else {
                Toast.makeText(getContext(), "Image cropping failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void launchImageCropper(Uri uri) {
        CropImageContractOptions options = new CropImageContractOptions(uri, new CropImageOptions()).setGuidelines(com.canhub.cropper.CropImageView.Guidelines.ON).setAspectRatio(1, 1).setFixAspectRatio(true);
        cropImage.launch(options);
    }

    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();
        FirebaseFirestore.getInstance().collection("users").document(userId).update("profileImageUri", imageUri.toString());
    }

    private void showEditProfileDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        final EditText etUsername = dialogView.findViewById(R.id.etEditUsername);
        final EditText etWeight = dialogView.findViewById(R.id.etEditWeight);
        final EditText etHeight = dialogView.findViewById(R.id.etEditHeight);
        final EditText etAge = dialogView.findViewById(R.id.etEditAge);

        DocumentSnapshot snapshot = sharedViewModel.getUserSnapshot().getValue();
        if (snapshot != null) {
            etUsername.setText(snapshot.getString("username"));
            etWeight.setText(String.valueOf(snapshot.getDouble("weight_kg")));
            etHeight.setText(String.valueOf(snapshot.getDouble("height_cm")));
            etAge.setText(String.valueOf(snapshot.getLong("age").intValue()));
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();
            String heightStr = etHeight.getText().toString().trim();
            String ageStr = etAge.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr) || TextUtils.isEmpty(ageStr)) {
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                double height = Double.parseDouble(heightStr);
                int age = Integer.parseInt(ageStr);

                Map<String, Object> updates = new HashMap<>();
                updates.put("username", username);
                updates.put("weight_kg", weight);
                updates.put("height_cm", height);
                updates.put("age", age);

                updateUserProfile(updates);

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for weight, height, and age.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void updateUserProfile(Map<String, Object> updates) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in to update your profile.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show());
    }

    private void initViews(View view) {
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEditProfile = view.findViewById(R.id.tvEditProfile);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnCamera = view.findViewById(R.id.btnCamera);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivArrowAccount = view.findViewById(R.id.ivArrowAccount);
        ivArrowNotifications = view.findViewById(R.id.ivArrowNotifications);
        ivArrowHelp = view.findViewById(R.id.ivArrowHelp);
        ivArrowAbout = view.findViewById(R.id.ivArrowAbout);
        layoutAccountDetails = view.findViewById(R.id.layoutAccountDetails);
        contentAccountInfo = view.findViewById(R.id.contentAccountInfo);
        tvDateJoined = view.findViewById(R.id.tvDateJoined);
        tvPrimaryLocation = view.findViewById(R.id.tvPrimaryLocation);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
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

    private void setupListeners(View view) {
        btnCamera.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
        tvEditProfile.setOnClickListener(v -> showEditProfileDialog());
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> toggleVisibility(layoutAccountDetails, ivArrowAccount));
        view.findViewById(R.id.btnNotifications).setOnClickListener(v -> toggleVisibility(layoutNotificationSettings, ivArrowNotifications));
        view.findViewById(R.id.btnSettings).setOnClickListener(v -> toggleVisibility(contentHelp, ivArrowHelp));
        view.findViewById(R.id.btnPrivacy).setOnClickListener(v -> toggleVisibility(contentAbout, ivArrowAbout));
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
        new AlertDialog.Builder(getContext()).setTitle("Log Out").setMessage("Are you sure you want to log out?").setPositiveButton("Log Out", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", null).show();
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
        }, hour, minute, true);
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
        FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid()).update(data).addOnFailureListener(e -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Failed to update preference", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendPasswordResetEmail() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            String email = currentUser.getEmail();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (getContext() == null) return; // Fragment not attached.
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to send reset email. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "sendPasswordResetEmail:failure", task.getException());
                        }
                    });
        } else {
            if (getContext() != null) {
                Toast.makeText(getContext(), "Could not find user's email.", Toast.LENGTH_SHORT).show();
            }
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
