package com.example.pathfitx;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;

public class EditExerciseDialog extends DialogFragment {

    private final Exercise exercise;
    private final int position;
    private final DialogListener listener;
    private int currentSets, currentReps, currentKg;

    // Interface to communicate back to HomeFragment
    public interface DialogListener {
        void onSave(Exercise exercise, int position);
        void onRemove(Exercise exercise, int position);
    }

    public EditExerciseDialog(Exercise exercise, int position, DialogListener listener) {
        this.exercise = exercise;
        this.position = position;
        this.listener = listener;
        this.currentSets = exercise.getSets();
        this.currentReps = exercise.getReps();
        this.currentKg = exercise.getKg();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_exercise, null);

        // Init Views
        ImageView imgExercise = view.findViewById(R.id.img_dialog_exercise);

        // UPDATED: Using imageResId for the local drawables from your classmate
        Glide.with(this)
                .load(exercise.getImageResId())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .into(imgExercise);

        ((TextView) view.findViewById(R.id.tv_dialog_exercise_name)).setText(exercise.getTitle());

        // Setup Inputs - Keeping your original logic
        setupInput(view.findViewById(R.id.input_sets), "SETS", currentSets, val -> currentSets = val);
        setupInput(view.findViewById(R.id.input_reps), "REPS", currentReps, val -> currentReps = val);
        setupInput(view.findViewById(R.id.input_kg), "KG", currentKg, val -> currentKg = val);

        // Button Listeners
        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btn_dialog_remove).setOnClickListener(v -> {
            listener.onRemove(exercise, position);
            dismiss();
        });
        view.findViewById(R.id.btn_dialog_save).setOnClickListener(v -> {
            exercise.setSets(currentSets);
            exercise.setReps(currentReps);
            exercise.setKg(currentKg);
            listener.onSave(exercise, position);
            dismiss();
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private interface ValueSetter { void set(int val); }

    private void setupInput(View includeView, String label, int initialValue, ValueSetter setter) {
        ((TextView) includeView.findViewById(R.id.tv_label)).setText(label);
        TextView tvValue = includeView.findViewById(R.id.tv_value);
        tvValue.setText(String.valueOf(initialValue));

        includeView.findViewById(R.id.btn_minus).setOnClickListener(v -> {
            int val = Integer.parseInt(tvValue.getText().toString());
            if (val > 0) { val--; tvValue.setText(String.valueOf(val)); setter.set(val); }
        });
        includeView.findViewById(R.id.btn_plus).setOnClickListener(v -> {
            int val = Integer.parseInt(tvValue.getText().toString());
            val++; tvValue.setText(String.valueOf(val)); setter.set(val);
        });
    }
}