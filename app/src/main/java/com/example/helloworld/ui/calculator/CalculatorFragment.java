package com.example.helloworld.ui.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.helloworld.databinding.FragmentCalculatorBinding;
import com.example.helloworld.domain.DrinkCalculator;

import java.util.ArrayList;
import java.util.List;

public class CalculatorFragment extends Fragment {

    private FragmentCalculatorBinding binding;
    private CalculatorViewModel viewModel;

    // Tracks whether each goal warning has already been shown this session
    private boolean dailyGoalWarningShown   = false;
    private boolean weeklyGoalWarningShown  = false;
    private boolean monthlyGoalWarningShown = false;

    // Holds the details for each preset drink button
    private static class DrinkPreset {
        String name;
        double volumeMl;
        double abv;
        int carbCalories;

        DrinkPreset(String name, double volumeMl, double abv, int carbCalories) {
            this.name         = name;
            this.volumeMl     = volumeMl;
            this.abv          = abv;
            this.carbCalories = carbCalories;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);

        buildDrinkPresetButtons();

        binding.calculateButton.setOnClickListener(v -> onManualCalculateClicked());

        binding.resetTotalButton.setOnClickListener(v -> {
            viewModel.resetSession();
            dailyGoalWarningShown   = false;
            weeklyGoalWarningShown  = false;
            monthlyGoalWarningShown = false;
            showResult("Session reset to 0.");
        });

        // Update the drinks counter and check all goals whenever the total changes
        viewModel.getTotalDrinks().observe(getViewLifecycleOwner(), total -> {
            binding.totalCountText.setText(String.valueOf(total));
            checkAllDrinkGoals(total);
        });

        // Update the calorie counter on screen whenever it changes
        viewModel.getTotalCalories().observe(getViewLifecycleOwner(), calories ->
                binding.totalCaloriesText.setText(calories + " kcal"));

        // Show a water reminder every 2 drinks
        viewModel.getDrinkCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0 && count % 2 == 0) showWaterReminder();
        });
    }

    // Checks all three goals against the current session total — same logic for each
    private void checkAllDrinkGoals(double sessionTotal) {
        SharedPreferences prefs = requireContext().getSharedPreferences("GoalsPrefs", android.content.Context.MODE_PRIVATE);

        if (!dailyGoalWarningShown) {
            float dailyGoal = prefs.getFloat("Daily_drinks", -1f);
            if (dailyGoal >= 0 && sessionTotal > dailyGoal) {
                dailyGoalWarningShown = true;
                showGoalWarning("Daily", dailyGoal, sessionTotal);
            }
        }

        if (!weeklyGoalWarningShown) {
            float weeklyGoal = prefs.getFloat("Weekly_drinks", -1f);
            if (weeklyGoal >= 0 && sessionTotal > weeklyGoal) {
                weeklyGoalWarningShown = true;
                showGoalWarning("Weekly", weeklyGoal, sessionTotal);
            }
        }

        if (!monthlyGoalWarningShown) {
            float monthlyGoal = prefs.getFloat("Monthly_drinks", -1f);
            if (monthlyGoal >= 0 && sessionTotal > monthlyGoal) {
                monthlyGoalWarningShown = true;
                showGoalWarning("Monthly", monthlyGoal, sessionTotal);
            }
        }
    }

    // Shows the goal exceeded pop-up — same style as the water reminder
    private void showGoalWarning(String period, float goal, double current) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(period + " Drink Goal Exceeded! 🚨")
                .setMessage("You've had " + current + " drinks this session, which is over your "
                        + period.toLowerCase() + " goal of " + (int) goal
                        + ". Consider slowing down and having some water.")
                .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Creates a tap-to-add button for each preset drink
    private void buildDrinkPresetButtons() {
        List<DrinkPreset> presets = new ArrayList<>();

        presets.add(new DrinkPreset("Vodka (35.5ml)",          35.5,  40.0,  0));
        presets.add(new DrinkPreset("Gin (35.5ml)",            35.5,  43.0,  0));
        presets.add(new DrinkPreset("Whiskey (35.5ml)",        35.5,  40.0,  0));
        presets.add(new DrinkPreset("Tequila (35.5ml)",        35.5,  50.5,  0));
        presets.add(new DrinkPreset("Liqueur (35.5ml)",        35.5,  15.0, 50));
        presets.add(new DrinkPreset("Fortified Wine (75ml)",   75.0,  20.0, 10));
        presets.add(new DrinkPreset("Wine (150ml)",           150.0,  15.0, 10));
        presets.add(new DrinkPreset("Beer (Pint)",            568.0,   5.0, 50));
        presets.add(new DrinkPreset("Malt Beverage (330ml)",  330.0,  15.0, 30));

        for (DrinkPreset drink : presets) {
            Button btn = new Button(getContext());

            int estimatedCalories = DrinkCalculator.calculateCalories(drink.volumeMl, drink.abv, drink.carbCalories);
            btn.setText(drink.name + "  (~" + estimatedCalories + " kcal)");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                viewModel.addDrink(drink.volumeMl, drink.abv, drink.carbCalories);
                double standardDrinks = DrinkCalculator.calculate(drink.volumeMl, drink.abv);
                showResult(drink.name + " added! (" + standardDrinks + " drinks, ~" + estimatedCalories + " kcal)");
            });

            binding.buttonContainer.addView(btn);
        }
    }

    private void onManualCalculateClicked() {
        String volumeText = binding.volumeInput.getText().toString().trim();
        String abvText    = binding.abvInput.getText().toString().trim();

        if (volumeText.isEmpty() || abvText.isEmpty()) {
            showResult("Please fill in both fields.");
            return;
        }

        try {
            double volume = Double.parseDouble(volumeText);
            double abv    = Double.parseDouble(abvText);

            if (volume <= 0 || abv <= 0 || abv > 100) {
                showResult("Please enter valid positive numbers (ABV max 100).");
                return;
            }

            double standardDrinks = DrinkCalculator.calculate(volume, abv);
            int calories = DrinkCalculator.calculateCalories(volume, abv, 0);

            viewModel.addDrink(volume, abv, 0);
            showResult("Added " + standardDrinks + " standard drinks (~" + calories + " kcal).");
        } catch (NumberFormatException e) {
            showResult("Invalid number format.");
        }
    }

    private void showWaterReminder() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Hydration Break! 💧")
                .setMessage("You've had 2 drinks since your last reminder. It's a good idea to have a glass of water now.")
                .setPositiveButton("I will!", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showResult(String message) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.resultText.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
