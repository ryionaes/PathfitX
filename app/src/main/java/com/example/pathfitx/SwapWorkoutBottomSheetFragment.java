package com.example.pathfitx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;

public class SwapWorkoutBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_OPTIONS = "options";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SELECTED_OPTION = "selected_option";

    private ArrayList<WorkoutType> options;
    private String title;
    private String selectedOption;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String option);
    }

    public static SwapWorkoutBottomSheetFragment newInstance(String title, ArrayList<WorkoutType> options, String selectedOption) {
        SwapWorkoutBottomSheetFragment fragment = new SwapWorkoutBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_OPTIONS, options);
        args.putString(ARG_SELECTED_OPTION, selectedOption);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            options = (ArrayList<WorkoutType>) getArguments().getSerializable(ARG_OPTIONS);
            title = getArguments().getString(ARG_TITLE);
            selectedOption = getArguments().getString(ARG_SELECTED_OPTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_swap_workout_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_bottom_sheet_title);
        tvTitle.setText(title);

        RecyclerView rvOptions = view.findViewById(R.id.rv_swap_options);
        SwapWorkoutAdapter adapter = new SwapWorkoutAdapter(options, selectedOption, option -> {
            selectedOption = option; // Update the selected option internally
            if (listener != null) {
                listener.onOptionSelected(selectedOption);
            }
            dismiss();
        });
        rvOptions.setAdapter(adapter);

        view.findViewById(R.id.iv_close_bottom_sheet).setOnClickListener(v -> dismiss());
    }
}