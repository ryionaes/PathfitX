package com.example.pathfitx;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class HomeScreen extends AppCompatActivity implements OnDateSelectedListener {

    private String selectedDate;
    private SharedViewModel sharedViewModel;
    private Date registrationDate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.attachListeners();

        sharedViewModel.getRegistrationDate().observe(this, date -> {
            this.registrationDate = date;
        });

        selectedDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        sharedViewModel.loadWorkoutForDate(selectedDate); // Initial load for today

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(selectedDate));
        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSelected(String date) {
        LocalDate selectedLocalDate = LocalDate.parse(date);
        if (registrationDate != null) {
            LocalDate registrationLocalDate = registrationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (selectedLocalDate.isBefore(registrationLocalDate)) {
                // Optionally, show a toast or message to the user
                return; // Don't update the date
            }
        }
        this.selectedDate = date;
        sharedViewModel.loadWorkoutForDate(date); // Load workout for newly selected date
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }
}
