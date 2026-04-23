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

    private static final String PREFS_NAME   = "GoalsPrefs";
    private static final String KEY_PERIOD   = "goal_period";
    private static final String KEY_DRINKS   = "goal_drinks";
    private static final String KEY_SPENDING = "goal_spending";
    private static final String KEY_CALORIES = "goal_calories";

    // Lookup arrays replace the if/else chain for period selection
    private static final int[]    PERIOD_IDS    = { R.id.rbDaily, R.id.rbWeekly, R.id.rbMonthly };
    private static final String[] PERIOD_LABELS = { "Daily",      "Weekly",      "Monthly"      };

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

        Button btnSaveGoals = findViewById(R.id.btnSaveGoals);
        btnSaveGoals.setOnClickListener(v -> saveGoals());

        loadGoals();
        updateSummary();
    }

    private void saveGoals() {
        int selectedId = rgPeriod.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a time period", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_PERIOD, periodLabel(selectedId));
        saveGoalField(editor, KEY_DRINKS,   etDrinks);
        saveGoalField(editor, KEY_SPENDING, etSpending);
        saveGoalField(editor, KEY_CALORIES, etCalories);
        editor.apply();

        Toast.makeText(this, periodLabel(selectedId) + " goals saved!", Toast.LENGTH_SHORT).show();
        updateSummary();
    }

    /** Saves a field as a float, or removes the key if the field is empty. */
    private void saveGoalField(SharedPreferences.Editor editor, String key, EditText field) {
        String text = field.getText().toString().trim();
        if (text.isEmpty()) {
            editor.remove(key);
        } else {
            editor.putFloat(key, Float.parseFloat(text));
        }
    }

    private void loadGoals() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Find matching period label and check its radio button
        String savedPeriod = prefs.getString(KEY_PERIOD, PERIOD_LABELS[0]);
        for (int i = 0; i < PERIOD_LABELS.length; i++) {
            if (PERIOD_LABELS[i].equals(savedPeriod)) {
                rgPeriod.check(PERIOD_IDS[i]);
                break;
            }
        }

        loadGoalField(prefs, KEY_DRINKS,   etDrinks);
        loadGoalField(prefs, KEY_SPENDING, etSpending);
        loadGoalField(prefs, KEY_CALORIES, etCalories);
    }

    /** Populates a field from prefs; leaves it blank if no value is stored. */
    private void loadGoalField(SharedPreferences prefs, String key, EditText field) {
        float value = prefs.getFloat(key, -1f);
        if (value >= 0) field.setText(formatValue(value));
    }

    private void updateSummary() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String period = prefs.getString(KEY_PERIOD, null);

        if (period == null) {
            tvCurrentGoals.setText("No goals set yet.");
            return;
        }

        // Each row: label, prefix, key, suffix
        String[][] goalDefs = {
            { "Drinks",   "",  KEY_DRINKS,   ""      },
            { "Spending", "€", KEY_SPENDING, ""      },
            { "Calories", "",  KEY_CALORIES, " kcal" }
        };

        StringBuilder sb = new StringBuilder("Current " + period + " goals:\n");
        for (String[] def : goalDefs) {
            float value = prefs.getFloat(def[2], -1f);
            if (value >= 0) {
                sb.append("  • ").append(def[0]).append(": ")
                  .append(def[1]).append(formatValue(value)).append(def[3]).append("\n");
            }
        }
        tvCurrentGoals.setText(sb.toString().trim());
    }

    /** Converts a radio button ID to its period label. */
    private String periodLabel(int radioId) {
        for (int i = 0; i < PERIOD_IDS.length; i++) {
            if (PERIOD_IDS[i] == radioId) return PERIOD_LABELS[i];
        }
        return PERIOD_LABELS[0];
    }

    /** Strips unnecessary decimals: 5.0 → "5", 5.5 → "5.5" */
    private String formatValue(float value) {
        return (value == Math.floor(value)) ? String.valueOf((int) value) : String.valueOf(value);
    }
}
