package com.example.pathfitx;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// This adapter provides the individual fragments (slides) to the ViewPager2.
public class OnboardingAdapter extends FragmentStateAdapter {

    // List of image resource IDs for the three slides.
    // NOTE: Ensure these drawable resources exist in res/drawable/.
    private final int[] slideImages = new int[]{
            R.drawable.onboarding_screen_1,
            R.drawable.onboarding_screen_2,
            R.drawable.onboarding_screen_3
    };

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Creates a new instance of the OnboardingFragment for the current slide.
        // It passes the corresponding image resource ID to the fragment.
        return OnboardingFragment.newInstance(slideImages[position]);
    }

    @Override
    public int getItemCount() {
        // Returns the total number of slides in the carousel.
        return slideImages.length;
    }
}