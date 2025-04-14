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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Calendar.CalendarAdapter;
import com.example.infosys_1d.DateHolder;
import com.example.infosys_1d.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
        eventCanvas.setOnEventClickListener(event -> openShowEventDialog(event));
        currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        loadWeeklyCalendar();
        loadTimetable();

        view.findViewById(R.id.previousWeekButton).setOnClickListener(v -> updateWeek(-1));
        view.findViewById(R.id.nextWeekButton).setOnClickListener(v -> updateWeek(1));

        fab.setOnClickListener(v -> openShowEventDialog(null));

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

    @Override
    public void onResume() {
        super.onResume();
        loadTimetable();  // ChatGPT: ensures canvas + adapter are refreshed
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

        timetableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                eventCanvas.invalidate();  // keep canvas in sync with scroll
            }
        }); // ChatGPT

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

    private void openShowEventDialog(@Nullable MyEvent eventToEdit) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);

        EditText eventName = dialogView.findViewById(R.id.eventName);
        TextView eventDate = dialogView.findViewById(R.id.eventDate);
        NumberPicker startHour = dialogView.findViewById(R.id.startHourPicker);
        NumberPicker startMinute = dialogView.findViewById(R.id.startMinutePicker);
        NumberPicker endHour = dialogView.findViewById(R.id.endHourPicker);
        NumberPicker endMinute = dialogView.findViewById(R.id.endMinutePicker);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        FloatingActionButton deleteButton = dialogView.findViewById(R.id.deleteButton);

        final LocalDate[] selectedDate = { (eventToEdit != null) ? eventToEdit.getDate() : LocalDate.now() };
        eventDate.setText(formatDate(selectedDate[0]));

        eventDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Event Date")
                    .setSelection(selectedDate[0].atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                selectedDate[0] = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                eventDate.setText(formatDate(selectedDate[0]));
            });

            picker.show(getParentFragmentManager(), "EVENT_DATE_PICKER");
        });

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) hours[i] = String.format("%02d", i);
        startHour.setMinValue(0); startHour.setMaxValue(23); startHour.setDisplayedValues(hours);
        endHour.setMinValue(0); endHour.setMaxValue(23); endHour.setDisplayedValues(hours);

        String[] minutes = new String[12];
        for (int i = 0; i < 12; i++) minutes[i] = String.format("%02d", i * 5);
        startMinute.setMinValue(0); startMinute.setMaxValue(11); startMinute.setDisplayedValues(minutes);
        endMinute.setMinValue(0); endMinute.setMaxValue(11); endMinute.setDisplayedValues(minutes);


        if (eventToEdit != null) {
            eventName.setText(eventToEdit.getTitle());
            int s = eventToEdit.getStartTime(), e = eventToEdit.getEndTime();
            startHour.setValue(s / 60); startMinute.setValue((s % 60) / 5);
            endHour.setValue(e / 60); endMinute.setValue((e % 60) / 5);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        saveButton.setText(eventToEdit != null ? "Update" : "Save");
        saveButton.setOnClickListener(v -> {
            String name = eventName.getText().toString().trim();
            int startTime = startHour.getValue() * 60 + (startMinute.getValue() * 5);
            int endTime = endHour.getValue() * 60 + (endMinute.getValue() * 5);

            if (name.isEmpty()) {
                eventName.setError("Please enter event name");
                return;
            }

            if (endTime <= startTime) {
                Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (eventToEdit == null) {
                MyEvent newEvent = new MyEvent(name, selectedDate[0], startTime, endTime);
                eventList.add(newEvent);
            } else {
                eventToEdit.setTitle(name);
                eventToEdit.setDate(selectedDate[0]);
                eventToEdit.setStartTime(startTime);
                eventToEdit.setEndTime(endTime);
            }

            timetableAdapter.setEventList(eventList);
            if (rowHeightPx > 0) {
                eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                eventCanvas.invalidate();
            }

            dialog.dismiss();
            Toast.makeText(getContext(), eventToEdit != null ? "Event updated!" : "Event saved!", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete", (d, which) -> {
                        eventList.remove(eventToEdit);
                        timetableAdapter.setEventList(eventList);
                        if (rowHeightPx > 0) {
                            eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                            eventCanvas.invalidate();
                        }
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private String formatDate(LocalDate date) {
        return String.format("Date: %02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }


}
