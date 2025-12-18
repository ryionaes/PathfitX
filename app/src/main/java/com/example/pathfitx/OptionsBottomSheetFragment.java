package com.example.pathfitx;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;

public class OptionsBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_OPTIONS = "options";
    private static final String ARG_TITLE = "title";
    private static final String ARG_SELECTED_OPTION = "selected_option";

    private ArrayList<String> options;
    private String title;
    private String selectedOption;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String option);
        void onSetAsDefault(String option);
    }

    public static OptionsBottomSheetFragment newInstance(String title, ArrayList<String> options, String selectedOption) {
        OptionsBottomSheetFragment fragment = new OptionsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArrayList(ARG_OPTIONS, options);
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
            options = getArguments().getStringArrayList(ARG_OPTIONS);
            title = getArguments().getString(ARG_TITLE);
            selectedOption = getArguments().getString(ARG_SELECTED_OPTION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_options_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitle = view.findViewById(R.id.tv_bottom_sheet_title);
        tvTitle.setText(title);

        RecyclerView rvOptions = view.findViewById(R.id.rv_options);
        OptionsAdapter adapter = new OptionsAdapter(options, selectedOption, option -> {
            selectedOption = option; // Update the selected option internally
        });
        rvOptions.setAdapter(adapter);

        view.findViewById(R.id.iv_close_bottom_sheet).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_set_for_workout).setOnClickListener(v -> {
            if (listener != null) {
                listener.onOptionSelected(selectedOption);
            }
            dismiss();
        });

        View setAsDefaultButton = view.findViewById(R.id.btn_set_as_default);
        if ("Equipment".equals(title)) {
            setAsDefaultButton.setVisibility(View.GONE);
        } else {
            setAsDefaultButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetAsDefault(selectedOption);
                }
                dismiss();
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog instanceof BottomSheetDialog) {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}