package com.example.helloworld.ui.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.databinding.FragmentCalculatorBinding;
import com.example.helloworld.domain.DrinkCalculator;

import java.util.ArrayList;
import java.util.List;

public class CalculatorFragment extends Fragment {

    private FragmentCalculatorBinding binding;

    private static class DrinkPreset {
        String name;
        double volumeMl;
        double abv;

        DrinkPreset(String name, double volumeMl, double abv) {
            this.name = name;
            this.volumeMl = volumeMl;
            this.abv = abv;
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private CalculatorViewModel viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(CalculatorViewModel.class);

        // 1. Setup the Preset Buttons
        setupDrinkButtons();

        // 2. Manual Calculate Button
        binding.calculateButton.setOnClickListener(v -> onManualCalculateClicked());

        // 3. Reset Button logic
        binding.resetTotalButton.setOnClickListener(v -> {
            viewModel.resetSession();
            showResult("Session reset to 0.");
        });

        // 4. Observe the total (using the ID from your XML: total_count_text)
        viewModel.getTotalDrinks().observe(getViewLifecycleOwner(), total -> {
            binding.totalCountText.setText(String.valueOf(total));
        });
        viewModel.getDrinkCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0 && count % 2 == 0) {
                showWaterReminder();
            }
        });
    }

    private void showWaterReminder() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Hydration Break! 💧")
                .setMessage("You've had 2 drinks since your last reminder. It's a good idea to have a glass of water now.")
                .setPositiveButton("I will!", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setupDrinkButtons() {
        List<DrinkPreset> presets = new ArrayList<>();
        // Irish standard sizes: Spirits (35.5ml), Wine (150ml), Beer/Pint (568ml)
        presets.add(new DrinkPreset("Vodka (35.5ml)", 35.5, 40.0));
        presets.add(new DrinkPreset("Gin (35.5ml)", 35.5, 43.0));
        presets.add(new DrinkPreset("Whiskey (35.5ml)", 35.5, 40.0));
        presets.add(new DrinkPreset("Tequila (35.5ml)", 35.5, 50.5));
        presets.add(new DrinkPreset("Liqueur (35.5ml)", 35.5, 15.0));
        presets.add(new DrinkPreset("Fortified Wine (75ml)", 75.0, 20.0));
        presets.add(new DrinkPreset("Wine (150ml)", 150.0, 15.0));
        presets.add(new DrinkPreset("Beer (Pint)", 568.0, 5.0));
        presets.add(new DrinkPreset("Malt Beverage", 330.0, 15.0));

        for (DrinkPreset drink : presets) {
            Button btn = new Button(getContext());
            btn.setText(drink.name);
            // Apply some margin so buttons aren't squashed
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                //Update the total in the ViewModel
                viewModel.addDrink(drink.volumeMl, drink.abv);

                // Can still show the individual result
                double result = DrinkCalculator.calculate(drink.volumeMl, drink.abv);
                showResult(drink.name + " added! (" + result + " drinks)");
            });
            binding.buttonContainer.addView(btn);
        }
    }

    private void onManualCalculateClicked() {
        String volumeText = binding.volumeInput.getText().toString().trim();
        String abvText = binding.abvInput.getText().toString().trim();


        if (volumeText.isEmpty() || abvText.isEmpty()) {
            showResult("Please fill in both fields.");
            return;
        }

        try {
            double volume = Double.parseDouble(volumeText);
            double abv = Double.parseDouble(abvText);

            if (volume <= 0 || abv <= 0 || abv > 100) {
                showResult("Please enter valid positive numbers (ABV max 100).");
                return;
            }

            double result = DrinkCalculator.calculate(volume, abv);
            viewModel.addDrink(volume, abv);
            showResult("Added " + result + " standard drinks.");
        } catch (NumberFormatException e) {
            showResult("Invalid number format.");
        }
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