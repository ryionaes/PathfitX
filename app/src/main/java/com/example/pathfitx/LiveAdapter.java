package com.example.pathfitx;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

    private List<Exercise> list;
    private OnSetCompletedListener listener;
    private volatile boolean isPaused = false;

    public interface OnSetCompletedListener {
        void onSetCompleted(Exercise exercise, boolean isChecked);
    }

    public LiveAdapter(List<Exercise> list, OnSetCompletedListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = list.get(position);
        holder.tvTitle.setText(exercise.getTitle());

        // UPDATED: Loading local resource ID instead of URL
        Glide.with(holder.itemView.getContext())
                .load(exercise.getImageResId())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .into(holder.img);

        holder.setsContainer.removeAllViews();

        for (int i = 1; i <= exercise.getSets(); i++) {
            final int currentSetIndex = i - 1;
            View setView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_set_row, holder.setsContainer, false);

            TextView tvSetNum = setView.findViewById(R.id.tv_set_number);
            TextView tvDetails = setView.findViewById(R.id.tv_set_details);
            ImageView btnCheck = setView.findViewById(R.id.btn_check_set);
            LinearLayout rowContainer = setView.findViewById(R.id.container_set_row);

            tvSetNum.setText("Set " + (currentSetIndex + 1));
            tvDetails.setText(exercise.getReps() + " reps • " + exercise.getKg() + " kg");

            boolean isSetCompleted = currentSetIndex < exercise.getCompletedSets();
            updateSetAppearance(rowContainer, tvSetNum, btnCheck, isSetCompleted);

            if (isPaused) {
                btnCheck.setAlpha(0.5f);
                btnCheck.setClickable(false);
            } else {
                btnCheck.setAlpha(1.0f);
                btnCheck.setClickable(true);
            }

            btnCheck.setOnClickListener(v -> {
                if (isPaused) {
                    Toast.makeText(holder.itemView.getContext(), "Workout Paused", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isSelected = rowContainer.getTag() != null && (boolean) rowContainer.getTag();

                if (!isSelected) {
                    if (currentSetIndex > exercise.getCompletedSets()) {
                        Toast.makeText(holder.itemView.getContext(), "Complete previous set first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateSetAppearance(rowContainer, tvSetNum, btnCheck, true);
                    if (listener != null) listener.onSetCompleted(exercise, true);
                } else {
                    if (currentSetIndex < exercise.getCompletedSets() - 1) {
                        Toast.makeText(holder.itemView.getContext(), "Undo subsequent sets first.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateSetAppearance(rowContainer, tvSetNum, btnCheck, false);
                    if (listener != null) listener.onSetCompleted(exercise, false);
                }
            });

            holder.setsContainer.addView(setView);
        }
    }

    private void updateSetAppearance(LinearLayout row, TextView setNum, ImageView check, boolean isCompleted) {
        if (isCompleted) {
            row.setBackgroundResource(R.drawable.bg_workout_done);
            setNum.setTextColor(Color.parseColor("#2E7D32"));
            check.setColorFilter(Color.parseColor("#2E7D32"));
            row.setTag(true);
        } else {
            row.setBackgroundResource(R.drawable.bg_card_goals);
            setNum.setTextColor(Color.parseColor("#102040"));
            check.clearColorFilter();
            row.setTag(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView img;
        LinearLayout setsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_live_exercise_title);
            img = itemView.findViewById(R.id.img_live_exercise);
            setsContainer = itemView.findViewById(R.id.layout_sets_container);
        }
    }
}