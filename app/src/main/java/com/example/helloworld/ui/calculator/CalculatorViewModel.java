package com.example.helloworld.ui.calculator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.helloworld.domain.DrinkCalculator;

public class CalculatorViewModel extends ViewModel {

    // Tracks the total number of standard drinks in the current session
    private final MutableLiveData<Double> _totalDrinks = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> _drinkCount = new MutableLiveData<>(0);
    public LiveData<Double> getTotalDrinks() {
        return _totalDrinks;
    }

    public LiveData<Integer> getDrinkCount() {
        return _drinkCount;
    }

    // Method to add a drink to the counter
    public void addDrink(double volume, double abv) {

        double newAmount = DrinkCalculator.calculate(volume, abv);
        Double currentTotal = _totalDrinks.getValue();
        if (currentTotal == null) currentTotal = 0.0;
        _totalDrinks.setValue(Math.round((currentTotal + newAmount) * 100.0) / 100.0);

        Integer currentCount = _drinkCount.getValue();
        if (currentCount == null) currentCount = 0;
        _drinkCount.setValue(currentCount + 1);
    }


    public void resetSession() {
        _totalDrinks.setValue(0.0);
    }
}
