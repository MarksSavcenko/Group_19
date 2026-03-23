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
        loadUserInfo();

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
    private void loadUserInfo() {
        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);

        String name = pref.getString("name", "");
        String age = pref.getString("age", "");
        String weight = pref.getString("weight", "");
        String height = pref.getString("height", "");
        String gender = pref.getString("gender", "");

        etName.setText(name);
        etAge.setText(age);
        etWeight.setText(weight);
        etHeight.setText(height);

        if (gender.equals("Male")){
            rgGender.check(R.id.rbMale);
        } else if (gender.equals("Female")){
            rgGender.check(R.id.rbFemale);
        }


    }

}
