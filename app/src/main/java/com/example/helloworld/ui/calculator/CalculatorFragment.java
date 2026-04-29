package com.example.helloworld.ui.calculator;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.helloworld.databinding.FragmentCalculatorBinding;
import com.example.helloworld.domain.DrinkCalculator;
import com.example.helloworld.domain.DrinkPriceStore;

import java.util.ArrayList;
import java.util.List;

public class CalculatorFragment extends Fragment {

    private FragmentCalculatorBinding binding;
    private CalculatorViewModel viewModel;
    private DrinkPriceStore priceStore;

    // -------------------------------------------------------------------------
    // Inner model
    // -------------------------------------------------------------------------

    private static class DrinkPreset {
        final String name;
        final double volumeMl;
        final double abv;

        DrinkPreset(String name, double volumeMl, double abv) {
            this.name     = name;
            this.volumeMl = volumeMl;
            this.abv      = abv;
        }
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel  = new androidx.lifecycle.ViewModelProvider(this).get(CalculatorViewModel.class);
        priceStore = new DrinkPriceStore(requireContext());

        setupDrinkButtons();

        binding.calculateButton.setOnClickListener(v -> onManualCalculateClicked());

        binding.resetTotalButton.setOnClickListener(v -> {
            viewModel.resetSession();
            showResult("Session reset to 0.");
        });

        // Observe standard drink total
        viewModel.getTotalDrinks().observe(getViewLifecycleOwner(), total ->
                binding.totalCountText.setText(String.valueOf(total)));

        // Observe spending total — update the spending TextView
        viewModel.getTotalSpending().observe(getViewLifecycleOwner(), spending ->
                binding.totalSpendingText.setText(String.format("€%.2f", spending)));

        // Water reminder every 2 drinks
        viewModel.getDrinkCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0 && count % 2 == 0) showWaterReminder();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupDrinkButtons() {
        List<DrinkPreset> presets = new ArrayList<>();
        presets.add(new DrinkPreset("Vodka (35.5ml)",        35.5,  40.0));
        presets.add(new DrinkPreset("Gin (35.5ml)",          35.5,  43.0));
        presets.add(new DrinkPreset("Whiskey (35.5ml)",      35.5,  40.0));
        presets.add(new DrinkPreset("Tequila (35.5ml)",      35.5,  50.5));
        presets.add(new DrinkPreset("Liqueur (35.5ml)",      35.5,  15.0));
        presets.add(new DrinkPreset("Fortified Wine (75ml)", 75.0,  20.0));
        presets.add(new DrinkPreset("Wine (150ml)",          150.0, 15.0));
        presets.add(new DrinkPreset("Beer (Pint)",           568.0,  5.0));
        presets.add(new DrinkPreset("Malt Beverage",         330.0, 15.0));

        for (DrinkPreset drink : presets) {
            Button btn = new Button(getContext());
            btn.setText(drink.name);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> showPriceDialog(drink));
            binding.buttonContainer.addView(btn);
        }
    }


    /**
     * Shows a dialog asking the user to confirm (or edit) the price for this drink.
     * Pre-fills with their last saved price, or an Irish market estimate.
     */
    private void showPriceDialog(DrinkPreset drink) {
        double suggestedPrice = priceStore.getPrice(drink.name);
        boolean isSaved       = priceStore.hasSavedPrice(drink.name);

        EditText priceInput = new EditText(requireContext());
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        priceInput.setHint("e.g. 6.50");
        if (suggestedPrice > 0) {
            priceInput.setText(String.format("%.2f", suggestedPrice));
            priceInput.selectAll();
        }

        // Label above the input explaining where the number came from
        TextView label = new TextView(requireContext());
        label.setPadding(16, 8, 16, 4);
        if (isSaved) {
            label.setText("Last price you paid for this drink:");
        } else {
            label.setText("Estimated Irish pub price — adjust if needed:");
        }

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 8);
        layout.addView(label);
        layout.addView(priceInput);

        new AlertDialog.Builder(requireContext())
                .setTitle("How much did " + drink.name + " cost?")
                .setView(layout)
                .setPositiveButton("Add drink", (dialog, which) -> {
                    double price = parsePrice(priceInput.getText().toString().trim());
                    // Remember this price for next time
                    priceStore.savePrice(drink.name, price);
                    // Record the drink + price in the ViewModel
                    viewModel.addDrink(drink.volumeMl, drink.abv, price);
                    double units = DrinkCalculator.calculate(drink.volumeMl, drink.abv);
                    showResult(drink.name + " added! (" + units + " units, €" +
                            String.format("%.2f", price) + ")");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Parses a price string; returns 0.0 on failure (no crash, just untracked). */
    private double parsePrice(String text) {
        if (text.isEmpty()) return 0.0;
        try {
            double val = Double.parseDouble(text);
            return Math.max(0.0, val);
        } catch (NumberFormatException e) {
            return 0.0;
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

            DrinkPreset custom = new DrinkPreset("Custom", volume, abv);
            showPriceDialog(custom);

        } catch (NumberFormatException e) {
            showResult("Invalid number format.");
        }
    }

    private void showResult(String message) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.resultText.setText(message);
    }

    private void showWaterReminder() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hydration Break! 💧")
                .setMessage("You've had 2 drinks since your last reminder. "
                        + "It's a good idea to have a glass of water now.")
                .setPositiveButton("I will!", (dialog, which) -> dialog.dismiss())
                .show();
    }
}