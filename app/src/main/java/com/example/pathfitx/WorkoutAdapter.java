package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<Exercise> exercises;
    private final OnExerciseInteractionListener listener;

    public interface OnExerciseInteractionListener {
        void onExerciseAdd(Exercise exercise, AddExerciseCallback callback);
        void onExerciseClick(Exercise exercise);
    }

    // Callback interface to report the result of the add operation
    public interface AddExerciseCallback {
        void onResult(boolean success);
    }

    public WorkoutAdapter(List<Exercise> list, OnExerciseInteractionListener listener) {
        this.exercises = new ArrayList<>(list);
        this.listener = listener;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = new ArrayList<>(exercises);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise item = exercises.get(position);
        holder.tvName.setText(item.getTitle());
        holder.tvTags.setText(item.getDetails());
        
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .into(holder.img);

        holder.btnAdd.setImageResource(R.drawable.ic_plus);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(item);
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseAdd(item, success -> {
                    if (success) {
                        holder.btnAdd.setImageResource(R.drawable.ic_check);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTags;
        ImageView img, btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_explore_name);
            tvTags = itemView.findViewById(R.id.tv_explore_tags);
            img = itemView.findViewById(R.id.img_explore_item);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
    }
}
