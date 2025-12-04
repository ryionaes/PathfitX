package com.example.pathfitx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<Exercise> originalList; // Keep a copy of all data
    private List<Exercise> filteredList; // The data currently being shown

    public WorkoutAdapter(List<Exercise> list) {
        this.originalList = list;
        this.filteredList = new ArrayList<>(list); // Start with full list
    }

    // Method to filter the list based on search text
    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (Exercise item : originalList) {
                if (item.getTitle().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
        }
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
        Exercise item = filteredList.get(position);
        holder.tvName.setText(item.getTitle());
        holder.tvTags.setText(item.getDetails()); // Reusing 'details' field for tags
        holder.img.setImageResource(item.getImageResId());

        holder.btnAdd.setOnClickListener(v -> {
            // Logic to add exercise goes here
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
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
