package com.example.pathfitx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.button.MaterialButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WelcomePage extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_TIME_KEY = "isFirstTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean(FIRST_TIME_KEY, true)) {
            Intent intent = new Intent(WelcomePage.this, HomeScreen.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome_page);

        ViewPager2 viewPager = findViewById(R.id.onboarding_view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_indicator);
        MaterialButton continueButton = findViewById(R.id.continue_button);

        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                }
        ).attach();

        setTabMargin(tabLayout, 12);

        continueButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();

            Intent intent = new Intent(WelcomePage.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });
    }

    private void setTabMargin(final TabLayout tabLayout, int marginDp) {
        try {
            int marginPx = (int) (marginDp * getResources().getDisplayMetrics().density);
            ViewGroup tabs = (ViewGroup) tabLayout.getChildAt(0);

            for (int i = 0; i < tabs.getChildCount(); i++) {
                View tab = tabs.getChildAt(i);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();

                if (i > 0) {
                    layoutParams.leftMargin = marginPx;
                } else {
                    layoutParams.leftMargin = 0;
                }

                tab.setLayoutParams(layoutParams);
                tabLayout.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}