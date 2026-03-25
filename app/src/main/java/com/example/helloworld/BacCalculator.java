package com.example.helloworld;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

public class BacCalculator extends AppCompatActivity {
    TextView  tResult;
    EditText etDrinks, etHours;
    Button btnCalculate;
    String savedWeight, savedGender, savedName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bac_calculator);
        //tUserInfo = findViewById(R.id.tUserInfo);
        tResult = findViewById(R.id.tResult);
        etDrinks = findViewById(R.id.etDrinks);
        etHours = findViewById(R.id.etHours);
        btnCalculate = findViewById(R.id.btnCalculate);


        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
         savedWeight = pref.getString("weight", "");
         savedGender = pref.getString("gender", "");

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String drinkText = etDrinks.getText().toString();
                String hourText = etHours.getText().toString();

                if (drinkText.isEmpty() || hourText.isEmpty()){
                    Toast.makeText(BacCalculator.this, "Enter drinks and hours", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (savedWeight.isEmpty() || savedGender.isEmpty()){
                    Toast.makeText(BacCalculator.this, "Please save User details first", Toast.LENGTH_SHORT).show();
                    return;
                }
                double drinks = Double.parseDouble(drinkText);
                double hours = Double.parseDouble(hourText);
                double weight = Double.parseDouble(savedWeight);
                double bac = calculateBAC(drinks, weight, savedGender, hours);

                tResult.setText("Estimated BAC: "+ String.format("%.3f", bac) + "\nEstimate only" );
            }
        });

        }
    private double calculateBAC(double drinks, double weight, String gender, double hours){
        double alcoholPerDrink = 10.0;
        double totalAlcohol = drinks * alcoholPerDrink;

        double r;
        if (gender.equals("Male")){
            r = 0.68;
        } else {
            r = 0.55;
        }
        double bac = (totalAlcohol / (weight * 1000 * r)) * 100 - (0.015 * hours);

        if (bac < 0 ){
            bac = 0;
        }
        return bac;



    }
}
