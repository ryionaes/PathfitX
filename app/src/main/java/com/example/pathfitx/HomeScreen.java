package com.example.pathfitx;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HomeScreen extends AppCompatActivity implements OnDateSelectedListener {

    private static final String TAG = "HomeScreen";
    private String selectedDate;

    // --- Centralized Data ---
    private ListenerRegistration userListener;
    private DocumentSnapshot userSnapshot;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        configureFirebase();
        attachUserListener();

        selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(selectedDate));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void configureFirebase() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }

    private void attachUserListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
            userListener = userDocRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "User listener failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    this.userSnapshot = snapshot;
                    // Notify the currently visible fragment of the update
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof UserUpdatable) {
                        ((UserUpdatable) currentFragment).onUserUpdate(snapshot);
                    }
                }
            });
        }
    }

    public DocumentSnapshot getUserSnapshot() {
        return userSnapshot;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment = null;
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = HomeFragment.newInstance(selectedDate);
        } else if (itemId == R.id.nav_workout) {
            selectedFragment = WorkoutFragment.newInstance(selectedDate);
        } else if (itemId == R.id.nav_progress) {
            selectedFragment = new ProgressFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        }

        if (selectedFragment != null) {
            loadFragment(selectedFragment);
        }
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onDateSelected(String date) {
        this.selectedDate = date;
    }
}