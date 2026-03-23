package com.example.helloworld;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserInfoActivity extends AppCompatActivity {
    EditText etName, etAge, etWeight, etHeight;
    RadioGroup rgGender;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedinstanceState){
        super.onCreate(savedinstanceState);
        setContentView(R.layout.user_info);
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        rgGender = findViewById(R.id.rgGender);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String age = etAge.getText().toString();
                String weight = etWeight.getText().toString();
                String height = etHeight.getText().toString();

                int selectedId = rgGender.getCheckedRadioButtonId();
                if (name.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty() || selectedId == -1) {
                    Toast.makeText(UserInfoActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton radioButton = findViewById(selectedId);
                String gender = radioButton.getText().toString();

                SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.putString("name", name);
                editor.putString("age", age);
                editor.putString("weight", weight);
                editor.putString("height", height);
                editor.putString("gender", gender);
                editor.apply();

                Toast.makeText(UserInfoActivity.this, "Saved", Toast.LENGTH_SHORT).show();


            }
        });

    }

}
