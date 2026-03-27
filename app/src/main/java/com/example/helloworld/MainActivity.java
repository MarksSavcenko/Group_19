package com.example.helloworld;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.helloworld.ui.calculator.CalculatorFragment;
import com.example.helloworld.ui.goals.GoalsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new CalculatorFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_calculator) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new CalculatorFragment())
                        .commit();
                return true;
            } else if (id == R.id.nav_goals) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new GoalsFragment())
                        .commit();
                return true;
            }
            return false;
        });
    }
}
