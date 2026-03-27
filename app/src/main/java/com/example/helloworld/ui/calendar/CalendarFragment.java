package com.example.helloworld.ui.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.helloworld.R;
import com.example.helloworld.domain.NightOutRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private NightOutRepository repository;
    private GridLayout grid;
    private TextView monthYearText;
    private CalendarDay selectedDay;
    private int currentYear;
    private int currentMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new NightOutRepository(requireContext());

        Calendar cal = Calendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH) + 1;

        monthYearText = view.findViewById(R.id.month_year_text);
        grid = view.findViewById(R.id.calendar_grid);

        view.findViewById(R.id.prev_button).setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 1) { currentMonth = 12; currentYear--; }
            selectedDay = null;
            buildMonth();
        });

        view.findViewById(R.id.next_button).setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 12) { currentMonth = 1; currentYear++; }
            selectedDay = null;
            buildMonth();
        });

        view.findViewById(R.id.add_button).setOnClickListener(v -> {
            if (selectedDay == null || selectedDay.day == 0) {
                Toast.makeText(getContext(), "Select a day first", Toast.LENGTH_SHORT).show();
                return;
            }
            repository.addNightOut(currentYear, currentMonth, selectedDay.day);
            selectedDay = null;
            buildMonth();
        });

        view.findViewById(R.id.remove_button).setOnClickListener(v -> {
            if (selectedDay == null || selectedDay.day == 0) {
                Toast.makeText(getContext(), "Select a day first", Toast.LENGTH_SHORT).show();
                return;
            }
            repository.removeNightOut(currentYear, currentMonth, selectedDay.day);
            selectedDay = null;
            buildMonth();
        });

        buildMonth();
    }

    private void buildMonth() {
        grid.removeAllViews();

        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthYearText.setText(monthNames[currentMonth - 1] + " " + currentYear);

        String[] dayHeaders = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String header : dayHeaders) {
            TextView tv = new TextView(getContext());
            tv.setText(header);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            tv.setPadding(0, 8, 0, 8);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            grid.addView(tv);
        }

        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth - 1, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = (firstDayOfWeek + 5) % 7;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        Set<Integer> nightsOut = repository.getNightsOutForMonth(currentYear, currentMonth);

        List<CalendarDay> dayCells = new ArrayList<>();
        for (int i = 0; i < offset; i++) {
            dayCells.add(new CalendarDay(0, false));
        }
        for (int d = 1; d <= daysInMonth; d++) {
            dayCells.add(new CalendarDay(d, nightsOut.contains(d)));
        }

        for (CalendarDay cell : dayCells) {
            TextView tv = new TextView(getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(14);
            tv.setPadding(0, 16, 0, 16);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);

            if (cell.day == 0) {
                tv.setText("");
            } else {
                tv.setText(String.valueOf(cell.day));
                if (cell.isNightOut) {
                    tv.setBackgroundColor(0xFF90CAF9);
                }
                if (selectedDay != null && cell.day == selectedDay.day) {
                    tv.setBackgroundColor(0xFFE0B0FF);
                }
                tv.setOnClickListener(v -> {
                    selectedDay = cell;
                    buildMonth();
                });
            }

            grid.addView(tv);
        }
    }
}
