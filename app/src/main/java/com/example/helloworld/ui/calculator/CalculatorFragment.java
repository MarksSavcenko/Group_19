package com.example.helloworld.ui.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.databinding.FragmentCalculatorBinding;
import com.example.helloworld.domain.DrinkCalculator;


public class    CalculatorFragment extends Fragment {

    private FragmentCalculatorBinding binding;

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

        binding.calculateButton.setOnClickListener(v -> onCalculateClicked());
    }

    private void onCalculateClicked() {
        String volumeText = binding.volumeInput.getText().toString().trim();
        String abvText = binding.abvInput.getText().toString().trim();


        if (volumeText.isEmpty() || abvText.isEmpty()) {
            showError("Please fill in both fields.");
            return;
        }


        double volume;
        double abv;
        try {
            volume = Double.parseDouble(volumeText);
            abv = Double.parseDouble(abvText);
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers.");
            return;
        }


        if (volume <= 0) {
            showError("Volume must be greater than zero.");
            return;
        }
        if (abv <= 0) {
            showError("ABV must be greater than zero.");
            return;
        }
        if (abv > 100) {
            showError("ABV cannot be more than 100%.");
            return;
        }


        double result = DrinkCalculator.calculate(volume, abv);
        showResult("That's approximately " + result + " standard drink(s).");
    }

    private void showResult(String message) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.resultText.setText(message);
    }

    private void showError(String message) {
        binding.resultCard.setVisibility(View.VISIBLE);
        binding.resultText.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
