package com.example.helloworld.ui.calculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.helloworld.domain.DrinkCalculator;

public class CalculatorViewModel extends ViewModel {

    // Tracks the total number of standard drinks in the current session
    private final MutableLiveData<Double> _totalDrinks = new MutableLiveData<>(0.0);
    public LiveData<Double> getTotalDrinks() {
        return _totalDrinks;
    }

    // Method to add a drink to the counter
    public void addDrink(double volume, double abv) {
        // Calculate the value using your DrinkCalculator domain logic
        double newAmount = DrinkCalculator.calculate(volume, abv);

        Double currentTotal = _totalDrinks.getValue();
        if (currentTotal == null) currentTotal = 0.0;

        // Update the total and round to 2 decimal places for a clean UI
        double updatedTotal = Math.round((currentTotal + newAmount) * 100.0) / 100.0;
        _totalDrinks.setValue(updatedTotal);
    }


    public void resetSession() {
        _totalDrinks.setValue(0.0);
    }
}
