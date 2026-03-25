package com.example.helloworld;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BacCalculator extends AppCompatActivity {
    TextView tUserInfo, tResult;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bac_calculator);
        tUserInfo = findViewById(R.id.tUserInfo);
        tResult = findViewById(R.id.tResult);
        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String savedName = pref.getString("name", "");
        String savedWeight = pref.getString("weight", "");
        String savedGender = pref.getString("gender", "");

        if (savedName.isEmpty() || savedWeight.isEmpty() || savedGender.isEmpty()){
            tUserInfo.setText("No saved User info found");
        } else {
            tUserInfo.setText("Name: " + savedName + "\nWeight: " + savedWeight + "kg\nGender: " + savedGender);
        }




    }
}
