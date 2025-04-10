package com.example.infosys_1d.Schedule;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import com.example.infosys_1d.Calendar.CalendarAdapter;
import com.example.infosys_1d.DateHolder;
import com.example.infosys_1d.Event.MyEvent;
import com.example.infosys_1d.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearTV;
    private RecyclerView calendarRecyclerView;
    private RecyclerView timetableRecyclerView;
    private LocalDate currentWeekStart;
    private LocalDate selectedDate = LocalDate.now();
    private DateHolder selectedDateHolder = new DateHolder(LocalDate.now());

    private TimetableAdapter timetableAdapter;

    private EventCanvasView eventCanvas;

    private com.google.android.material.floatingactionbutton.FloatingActionButton fab;

    private int rowHeightPx = -1;
    private static List<MyEvent> eventList = new ArrayList<>();

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
//        timetableRecyclerView.setHasFixedSize(true);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTimetable();

        if (rowHeightPx > 0) {
            eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
            eventCanvas.invalidate();
        }
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
        } else {
            timetableAdapter.setEventList(eventList);
            timetableAdapter.notifyDataSetChanged();
        }

        while (timetableRecyclerView.getItemDecorationCount() > 0) {
            timetableRecyclerView.removeItemDecorationAt(0);
        }
        timetableRecyclerView.addItemDecoration(new TimetableItemDecoration(getContext()));

        timetableRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View rowView = timetableRecyclerView.getLayoutManager().findViewByPosition(0);
                if (rowView != null && rowView.getHeight() > 0) {
                    rowHeightPx = rowView.getHeight();
                    eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                    eventCanvas.invalidate();
                    timetableRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        Log.d("ScheduleFragment", "loadTimetable: eventList size = " + eventList.size());
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

//        DateHolder selectedDateHolder = new DateHolder(this.selectedDate); // or LocalDate.now()

//        final LocalDate[] selectedDateHolder = {this.selectedDate};
        eventDate.setOnClickListener(v -> {
            Log.d("ScheduleFragment", "Date TextView clicked");
            selectDateDialog(eventDate, selectedDateHolder);
        });
        eventDate.setText(formatDate(selectedDateHolder.getDate()));




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

            MyEvent newEvent = new MyEvent(name, selectedDateHolder.getDate(), startTime, endTime);
            eventList.add(newEvent);
            timetableAdapter.setEventList(eventList);

            if (!newEvent.getDate().isBefore(currentWeekStart) && !newEvent.getDate().isAfter(currentWeekStart.plusDays(6))) {
                if (rowHeightPx > 0) {
                    eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                    eventCanvas.invalidate();
                }
            }


            Toast.makeText(getContext(), "Event \"" + name + "\" saved!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void selectDateDialog(TextView eventDate, DateHolder selectedDateHolder) {
        LocalDate currentDate = selectedDateHolder.getDate();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue() - 1; // Java Calendar months are 0-based
        int day = currentDate.getDayOfMonth();

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                // Update the DateHolder value
                LocalDate pickedDate = LocalDate.of(year, month + 1, dayOfMonth);
                selectedDateHolder.setDate(pickedDate);

                // Update the UI
                eventDate.setText(formatDate(pickedDate));
            }
        }, year, month, day);

        dialog.show();
    }

    private String formatDate(LocalDate date) {
        return String.format("Date: %02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }


}
