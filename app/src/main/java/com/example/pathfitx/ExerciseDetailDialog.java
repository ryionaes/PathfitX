package com.example.pathfitx;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ExerciseDetailDialog extends DialogFragment {

    private final Exercise exercise;
    private OnAddExerciseListener listener;

    public interface OnAddExerciseListener {
        void onAddExercise(Exercise exercise);
    }

    public ExerciseDetailDialog(Exercise exercise) {
        this.exercise = exercise;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnAddExerciseListener) {
            listener = (OnAddExerciseListener) getParentFragment();
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAddExerciseListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exercise_details, null);

        TextView tvExerciseName = view.findViewById(R.id.tv_exercise_name_detail);
        TextView tvExerciseTags = view.findViewById(R.id.tv_exercise_tags_detail);
        ImageView ivExerciseImage = view.findViewById(R.id.iv_exercise_image_detail);
        TextView tvMuscleTargets = view.findViewById(R.id.tv_muscle_targets_detail);
        ImageButton btnClose = view.findViewById(R.id.btn_close_detail);
        Button btnAdd = view.findViewById(R.id.btn_add_to_workout_detail);

        tvExerciseName.setText(exercise.getTitle());
        tvExerciseTags.setText(exercise.getTags() + " • " + exercise.getBodyPart());

        StringBuilder muscleTargetsText = new StringBuilder();
        for (String muscle : exercise.getMuscleTargets()) {
            muscleTargetsText.append("• ").append(muscle).append("\n");
        }
        tvMuscleTargets.setText(muscleTargetsText.toString().trim());

        // TODO: Load real image with Glide
        // ivExerciseImage.setImageResource(exercise.getImageResId());

        builder.setView(view);
        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddExercise(exercise);
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }
}
