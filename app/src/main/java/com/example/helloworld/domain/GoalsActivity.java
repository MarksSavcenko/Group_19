package com.example.helloworld.domain;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld.R;

public class GoalsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "GoalsPrefs";

    // Each period gets its own set of keys so goals are stored separately
    private static final String[] PERIOD_LABELS = { "Daily", "Weekly", "Monthly" };
    private static final int[]    PERIOD_IDS    = { R.id.rbDaily, R.id.rbWeekly, R.id.rbMonthly };

    private RadioGroup rgPeriod;
    private EditText   etDrinks, etSpending, etCalories;
    private TextView   tvCurrentGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        rgPeriod       = findViewById(R.id.rgPeriod);
        etDrinks       = findViewById(R.id.etGoalDrinks);
        etSpending     = findViewById(R.id.etGoalSpending);
        etCalories     = findViewById(R.id.etGoalCalories);
        tvCurrentGoals = findViewById(R.id.tvCurrentGoals);

        Button saveButton = findViewById(R.id.btnSaveGoals);
        saveButton.setOnClickListener(v -> onSaveButtonClicked());

        // When the user switches period, load that period's saved goals into the form
        rgPeriod.setOnCheckedChangeListener((group, checkedId) -> {
            loadGoalsForPeriod(getPeriodLabel(checkedId));
            refreshGoalsSummary();
        });

        // Start on Daily by default
        rgPeriod.check(R.id.rbDaily);
    }

    private void onSaveButtonClicked() {
        int selectedId = rgPeriod.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please pick a time period first!", Toast.LENGTH_SHORT).show();
            return;
        }

        String chosenPeriod = getPeriodLabel(selectedId);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        // Save each field under a period-specific key e.g. "Daily_goal_drinks"
        saveFieldToPrefs(editor, periodKey(chosenPeriod, "drinks"),   etDrinks);
        saveFieldToPrefs(editor, periodKey(chosenPeriod, "spending"), etSpending);
        saveFieldToPrefs(editor, periodKey(chosenPeriod, "calories"), etCalories);
        editor.apply();

        Toast.makeText(this, chosenPeriod + " goals saved!", Toast.LENGTH_SHORT).show();
        refreshGoalsSummary();
    }

    // Saves a field as a float, or removes the key if the user left it blank
    private void saveFieldToPrefs(SharedPreferences.Editor editor, String key, EditText field) {
        String userInput = field.getText().toString().trim();
        if (userInput.isEmpty()) {
            editor.remove(key);
        } else {
            editor.putFloat(key, Float.parseFloat(userInput));
        }
    }

    // Fills the form with the saved goals for the selected period
    private void loadGoalsForPeriod(String period) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        fillFieldFromPrefs(prefs, periodKey(period, "drinks"),   etDrinks);
        fillFieldFromPrefs(prefs, periodKey(period, "spending"), etSpending);
        fillFieldFromPrefs(prefs, periodKey(period, "calories"), etCalories);
    }

    // Puts a saved number back into a field, or clears it if nothing was saved
    private void fillFieldFromPrefs(SharedPreferences prefs, String key, EditText field) {
        float savedValue = prefs.getFloat(key, -1f);
        if (savedValue >= 0) {
            field.setText(formatNumber(savedValue));
        } else {
            field.setText("");
        }
    }

    // Shows all three periods' goals in the summary at the bottom
    private void refreshGoalsSummary() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        StringBuilder summary = new StringBuilder();

        for (String period : PERIOD_LABELS) {
            float drinks   = prefs.getFloat(periodKey(period, "drinks"),   -1f);
            float spending = prefs.getFloat(periodKey(period, "spending"), -1f);
            float calories = prefs.getFloat(periodKey(period, "calories"), -1f);

            // Only show the period heading if at least one goal has been set
            if (drinks >= 0 || spending >= 0 || calories >= 0) {
                summary.append(period).append(":\n");
                if (drinks   >= 0) summary.append("  • Drinks: ").append(formatNumber(drinks)).append("\n");
                if (spending >= 0) summary.append("  • Spending: €").append(formatNumber(spending)).append("\n");
                if (calories >= 0) summary.append("  • Calories: ").append(formatNumber(calories)).append(" kcal\n");
                summary.append("\n");
            }
        }

        if (summary.length() == 0) {
            tvCurrentGoals.setText("No goals set yet.");
        } else {
            tvCurrentGoals.setText(summary.toString().trim());
        }
    }

    // Builds the prefs key for a given period and goal type e.g. "Daily_drinks"
    private String periodKey(String period, String goalType) {
        return period + "_" + goalType;
    }

    // Looks up the period label for a radio button ID
    private String getPeriodLabel(int radioButtonId) {
        for (int i = 0; i < PERIOD_IDS.length; i++) {
            if (PERIOD_IDS[i] == radioButtonId) return PERIOD_LABELS[i];
        }
        return PERIOD_LABELS[0];
    }

    // Shows "5" instead of "5.0" for whole numbers
    private String formatNumber(float value) {
        return (value == Math.floor(value)) ? String.valueOf((int) value) : String.valueOf(value);
    }
}
