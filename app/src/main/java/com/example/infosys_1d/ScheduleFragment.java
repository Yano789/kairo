package com.example.infosys_1d;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearTV;
    private RecyclerView calendarRecyclerView;
    private RecyclerView timetableRecyclerView;
    private LocalDate currentWeekStart;
    private LocalDate selectedDate = LocalDate.now();
    private TimetableAdapter timetableAdapter;

    private EventCanvasView eventCanvas;

    private com.google.android.material.floatingactionbutton.FloatingActionButton fab;

    private int rowHeightPx = -1;
    private List<MyEvent> eventList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        calendarRecyclerView = view.findViewById(R.id.weekRecyclerView);
        timetableRecyclerView = view.findViewById(R.id.timetableRecyclerView);
        eventCanvas = view.findViewById(R.id.eventCanvas);

        monthYearTV = view.findViewById(R.id.monthYearTV);
        fab = view.findViewById(R.id.fab);

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 8));
        timetableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timetableRecyclerView.setHasFixedSize(true);

        timetableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                eventCanvas.scrollBy(dx, dy);
            }
        });

        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        loadWeeklyCalendar();
        loadTimetable();

        view.findViewById(R.id.previousWeekButton).setOnClickListener(v -> updateWeek(-1));
        view.findViewById(R.id.nextWeekButton).setOnClickListener(v -> updateWeek(1));

        fab.setOnClickListener(v -> openAddEventDialog());

        return view;
    }

    private void loadWeeklyCalendar() {
        ArrayList<LocalDate> weekDays = new ArrayList<>();
        weekDays.add(null);

        for (int i = 0; i < 7; i++) {
            weekDays.add(currentWeekStart.plusDays(i));
        }

        calendarRecyclerView.setAdapter(new CalendarAdapter(weekDays, this));
        monthYearTV.setText(currentWeekStart.getMonth() + " " + currentWeekStart.getYear());
    }

    private void loadTimetable() {
        if (timetableAdapter == null) {
            timetableAdapter = new TimetableAdapter(getContext(), eventList, currentWeekStart);
            timetableRecyclerView.setAdapter(timetableAdapter);
            timetableRecyclerView.addItemDecoration(new TimetableItemDecoration(getContext()));
        } else {
            timetableAdapter.setEventList(eventList);
        }

        timetableRecyclerView.post(() -> {
            View rowView = timetableRecyclerView.getLayoutManager().findViewByPosition(0);
            if (rowView != null && rowView.getHeight() > 0) {
                rowHeightPx = rowView.getHeight();
                eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
            } else {
                timetableRecyclerView.postDelayed(this::loadTimetable, 50);
            }
        });
    }




    private void updateWeek(int weekChange) {
        currentWeekStart = currentWeekStart.plusWeeks(weekChange);
        loadWeeklyCalendar();
        loadTimetable();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            selectedDate = date;
            Toast.makeText(getContext(), "Selected: " + selectedDate.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openAddEventDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);

        EditText eventName = dialogView.findViewById(R.id.eventName);
        TextView eventDate = dialogView.findViewById(R.id.eventDate);
        NumberPicker startHour = dialogView.findViewById(R.id.startHourPicker);
        NumberPicker startMinute = dialogView.findViewById(R.id.startMinutePicker);
        NumberPicker endHour = dialogView.findViewById(R.id.endHourPicker);
        NumberPicker endMinute = dialogView.findViewById(R.id.endMinutePicker);

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = (i < 10 ? "0" : "") + i;
        }
        startHour.setMinValue(0); startHour.setMaxValue(23);
        startHour.setDisplayedValues(hours);
        endHour.setMinValue(0); endHour.setMaxValue(23);
        endHour.setDisplayedValues(hours);

        String[] minutes = new String[12];
        for (int i = 0; i < 12; i++) {
            minutes[i] = String.format("%02d", i * 5);
        }
        startMinute.setMinValue(0); startMinute.setMaxValue(11);
        startMinute.setDisplayedValues(minutes);
        endMinute.setMinValue(0); endMinute.setMaxValue(11);
        endMinute.setDisplayedValues(minutes);

        final LocalDate[] selectedDateHolder = {this.selectedDate};
        eventDate.setText("Date: " + selectedDateHolder[0].toString());

        eventDate.setOnClickListener(v -> selectDateDialog(eventDate));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        Button saveButton = dialogView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            int startH = startHour.getValue();
            int startM = startMinute.getValue() * 5;
            int endH = endHour.getValue();
            int endM = endMinute.getValue() * 5;

            if (name.isEmpty()) {
                eventName.setError("Please enter event name");
                return;
            }

            int startTime = startH * 60 + startM;
            int endTime = endH * 60 + endM;

            if (endTime <= startTime) {
                Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            MyEvent newEvent = new MyEvent(name, selectedDateHolder[0], startTime, endTime);
            eventList.add(newEvent);
            timetableAdapter.setEventList(eventList);

            if (!newEvent.getDate().isBefore(currentWeekStart) && !newEvent.getDate().isAfter(currentWeekStart.plusDays(6))) {
                eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
            }

            Toast.makeText(getContext(), "Event \"" + name + "\" saved!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void selectDateDialog(TextView eventDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = String.format("Date: %02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
            eventDate.setText(formattedDate);
            selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
        }, year, month, day);

        dialog.show();
    }

}
