package com.example.helloworld.ui.goals;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.helloworld.R;

public class DialogSetGoal extends DialogFragment {

    public interface Listener {
        void onGoalConfirmed(Goal.GoalType type, Goal.PeriodType period, double limit);
    }

    public static DialogSetGoal newInstance(@Nullable Goal existing) {
        DialogSetGoal dialog = new DialogSetGoal();
        if (existing != null) {
            Bundle args = new Bundle();
            args.putString("goal_type",   existing.getGoalType().name());
            args.putString("period_type", existing.getPeriodType().name());
            args.putDouble("limit",       existing.getLimit());
            dialog.setArguments(args);
        }
        return dialog;
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View     view          = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_goal, null);
        Spinner  spinnerType   = view.findViewById(R.id.spinnerGoalType);
        Spinner  spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        EditText etLimit       = view.findViewById(R.id.etGoalLimit);

        spinnerType.setAdapter(labelsAdapter(Goal.GoalType.values(), v -> v.getLabel()));
        spinnerPeriod.setAdapter(labelsAdapter(Goal.PeriodType.values(), v -> v.getLabel()));

        // Pre-fill if editing
        Bundle args = getArguments();
        if (args != null) {
            spinnerType.setSelection(Goal.GoalType.valueOf(args.getString("goal_type")).ordinal());
            spinnerPeriod.setSelection(Goal.PeriodType.valueOf(args.getString("period_type")).ordinal());
            etLimit.setText(String.valueOf(args.getDouble("limit")));
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Set Goal")
                .setView(view)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSave.setOnClickListener(v -> {
                String raw = etLimit.getText().toString().trim();
                if (TextUtils.isEmpty(raw)) {
                    etLimit.setError("Enter a limit");
                    return;
                }
                double limit;
                try {
                    limit = Double.parseDouble(raw);
                }
                catch (NumberFormatException e) {
                    etLimit.setError("Invalid number");
                    return;
                }
                if (limit <= 0) {
                    etLimit.setError("Must be greater than 0");
                    return;
                }

                Goal.GoalType   type   = Goal.GoalType.values()  [spinnerType.getSelectedItemPosition()];
                Goal.PeriodType period = Goal.PeriodType.values()[spinnerPeriod.getSelectedItemPosition()];

                Listener listener = (getParentFragment() instanceof Listener)
                        ? (Listener) getParentFragment()
                        : (requireActivity() instanceof Listener) ? (Listener) requireActivity() : null;

                if (listener != null) listener.onGoalConfirmed(type, period, limit);
                dialog.dismiss();
            });
        });

        return dialog;
    }
    interface LabelExtractor<T> {
        String getLabel(T item);
    }

    private <T> ArrayAdapter<String> labelsAdapter(T[] items, LabelExtractor<T> extractor){
        String[] labels = new String[items.length];
        for (int i = 0; i < items.length; i++) labels[i] = extractor.getLabel(items[i]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
