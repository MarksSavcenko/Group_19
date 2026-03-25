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

    // Data structure to hold our drink presets
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDrinkButtons();
    }

    private void setupDrinkButtons() {
        List<DrinkPreset> presets = new ArrayList<>();
        // Using Irish standard sizes: Spirits (35.5ml), Wine (150ml), Beer/Pint (568ml)
        presets.add(new DrinkPreset("Vodka (35.5ml)", 35.5, 40.0));
        presets.add(new DrinkPreset("Gin (35.5ml)", 35.5, 43.0));
        presets.add(new DrinkPreset("Whiskey (35.5ml)", 35.5, 40.0));
        presets.add(new DrinkPreset("Tequila (35.5ml)", 35.5, 50.5));
        presets.add(new DrinkPreset("Liqueur (35.5ml)", 35.5, 15.0));
        presets.add(new DrinkPreset("Fortified Wine (75ml)", 75.0, 20.0));
        presets.add(new DrinkPreset("Wine (150ml)", 150.0, 15.0));
        presets.add(new DrinkPreset("Beer (Pint)", 568.0, 5.0));
        presets.add(new DrinkPreset("Malt Beverage", 330.0, 15.0));

        // Assuming you have a LinearLayout with id 'buttonContainer' in your XML
        // Otherwise, you can inject them into binding.getRoot() or a specific layout
        for (DrinkPreset drink : presets) {
            Button btn = new Button(getContext());
            btn.setText(drink.name);
            btn.setOnClickListener(v -> {
                double result = DrinkCalculator.calculate(drink.volumeMl, drink.abv);
                showResult(drink.name + " is " + result + " standard Irish drinks.");
            });
            binding.buttonContainer.addView(btn);
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