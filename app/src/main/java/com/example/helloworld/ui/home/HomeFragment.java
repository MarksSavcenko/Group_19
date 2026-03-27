package com.example.helloworld.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.content.Intent;
import com.example.helloworld.R;
import com.example.helloworld.domain.BacCalculator;

import com.example.helloworld.domain.UserInfoActivity;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_calculator).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.calculatorFragment));

        view.findViewById(R.id.btn_calendar).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.calendarFragment));
        view.findViewById(R.id.btn_UserInfoActivity).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            startActivity(intent);
        });
        view.findViewById(R.id.btn_BacCalculator).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BacCalculator.class);
            startActivity(intent);
        });





    }
    }