package com.example.infosys_1d.Schedule;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private static final String TAG = "ScheduleFragment";
    private static final String KEY_SCROLL_STATE = "scroll_state";
    private TextView monthYearTV;
    private RecyclerView calendarRecyclerView;
    private RecyclerView timetableRecyclerView;
    private LocalDate currentWeekStart;
    private LocalDate selectedDate = LocalDate.now();
    private String userEmail;
    private Parcelable scrollState;

    private TimetableAdapter timetableAdapter;
    private EventCanvasView eventCanvas;
    private FloatingActionButton fab;

    private int rowHeightPx = -1;
    private List<Event> eventList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Get user email
        userEmail = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("user_email", "");
        if (userEmail.isEmpty()) {
            Log.w(TAG, "No user email found");
            Toast.makeText(getContext(), "Please log in to view your timetable", Toast.LENGTH_SHORT).show();
        }

        calendarRecyclerView = view.findViewById(R.id.weekRecyclerView);
        timetableRecyclerView = view.findViewById(R.id.timetableRecyclerView);
        eventCanvas = view.findViewById(R.id.eventCanvas);
        monthYearTV = view.findViewById(R.id.monthYearTV);
        fab = view.findViewById(R.id.fab);

        calendarRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 8));
        timetableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timetableRecyclerView.setNestedScrollingEnabled(false);

        eventCanvas.setOnEventClickListener(this::openShowEventDialog);

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
        timetableRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        timetableRecyclerView.setNestedScrollingEnabled(false);
        loadTimetable();
        if (scrollState != null && timetableRecyclerView.getLayoutManager() != null) {
            timetableRecyclerView.getLayoutManager().onRestoreInstanceState(scrollState);
            Log.d(TAG, "Restored scroll state");
        }
        timetableRecyclerView.invalidate();
        timetableRecyclerView.requestLayout();
        timetableRecyclerView.post(() -> {
            boolean canScroll = timetableRecyclerView.canScrollVertically(1) || timetableRecyclerView.canScrollVertically(-1);
            Log.d(TAG, "Post-resume: Can scroll = " + canScroll + ", Child count = " + timetableRecyclerView.getChildCount());
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timetableRecyclerView.getLayoutManager() != null) {
            scrollState = timetableRecyclerView.getLayoutManager().onSaveInstanceState();
            Log.d(TAG, "Saved scroll state");
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
        eventList.clear();
        if (!userEmail.isEmpty()) {
            List<Event> allEvents = UserRepository.getUserEvents(userEmail);
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            for (Event event : allEvents) {
                try {
                    LocalDate eventDate = LocalDate.parse(event.getDate());
                    if (!eventDate.isBefore(currentWeekStart) && !eventDate.isAfter(weekEnd)) {
                        eventList.add(event);
                        Log.d(TAG, "Added event: " + event.getTitle() + ", Date: " + event.getDate() + ", Tags: " + event.getTags());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing event date: " + event.getTitle() + ", Date: " + event.getDate() + ", Error: " + e.getMessage());
                }
            }
            Log.d(TAG, "Loaded " + eventList.size() + " events for week starting " + currentWeekStart);
        }

        timetableAdapter = new TimetableAdapter(getContext(), eventList, currentWeekStart);
        timetableRecyclerView.setAdapter(timetableAdapter);

        while (timetableRecyclerView.getItemDecorationCount() > 0) {
            timetableRecyclerView.removeItemDecorationAt(0);
        }
        timetableRecyclerView.addItemDecoration(new TimetableItemDecoration(getContext()));

        timetableRecyclerView.clearOnScrollListeners();
        timetableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                eventCanvas.invalidate();
            }
        });

        timetableRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View rowView = timetableRecyclerView.getLayoutManager().findViewByPosition(0);
                if (rowView != null && rowView.getHeight() > 0) {
                    rowHeightPx = rowView.getHeight();
                    eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                    eventCanvas.invalidate();
                    timetableRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Log.d(TAG, "Set rowHeightPx = " + rowHeightPx);
                }
            }
        });

        timetableRecyclerView.requestLayout();
        timetableRecyclerView.post(() -> {
            boolean canScroll = timetableRecyclerView.canScrollVertically(1) || timetableRecyclerView.canScrollVertically(-1);
            Log.d(TAG, "Post-layout: Can scroll = " + canScroll + ", Child count = " + timetableRecyclerView.getChildCount());
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

    private void openShowEventDialog(@Nullable Event eventToEdit) {
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

        boolean isPersonalEvent = eventToEdit != null && eventToEdit.getTags().contains("personal");

        final LocalDate[] selectedDate = {(eventToEdit != null) ? LocalDate.parse(eventToEdit.getDate()) : LocalDate.now()};
        eventDate.setText(formatDate(selectedDate[0]));

        if (isPersonalEvent || eventToEdit == null) {
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
        } else {
            eventDate.setEnabled(false);
        }

        String[] hours = new String[24];
        for (int i = 0; i < 24; i++) hours[i] = String.format("%02d", i);
        startHour.setMinValue(0); startHour.setMaxValue(23); startHour.setDisplayedValues(hours);
        endHour.setMinValue(0); endHour.setMaxValue(23); endHour.setDisplayedValues(hours);

        String[] minutes = new String[12];
        for (int i = 0; i < 12; i++) minutes[i] = String.format("%02d", i * 5);
        startMinute.setMinValue(0); startMinute.setMaxValue(11); startMinute.setDisplayedValues(minutes);
        endMinute.setMinValue(0); endMinute.setMaxValue(11); endMinute.setDisplayedValues(minutes);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        if (eventToEdit != null) {
            eventName.setText(eventToEdit.getTitle());
            String startTimeStr = timeFormat.format(eventToEdit.getStartTime());
            String endTimeStr = timeFormat.format(eventToEdit.getEndTime());
            String[] startParts = startTimeStr.split(":");
            String[] endParts = endTimeStr.split(":");
            startHour.setValue(Integer.parseInt(startParts[0]));
            startMinute.setValue(Integer.parseInt(startParts[1]) / 5);
            endHour.setValue(Integer.parseInt(endParts[0]));
            endMinute.setValue(Integer.parseInt(endParts[1]) / 5);

            if (!isPersonalEvent) {
                eventName.setEnabled(false);
                startHour.setEnabled(false);
                startMinute.setEnabled(false);
                endHour.setEnabled(false);
                endMinute.setEnabled(false);
                saveButton.setVisibility(View.GONE);
            }
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            startHour.setValue(9);
            startMinute.setValue(0);
            endHour.setValue(10);
            endMinute.setValue(0);
            deleteButton.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialog.show();

        saveButton.setText(eventToEdit != null && isPersonalEvent ? "Update" : "Save");
        saveButton.setOnClickListener(v -> {
            if (userEmail.isEmpty()) {
                Toast.makeText(getContext(), "Please log in to save events", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            if (!isPersonalEvent && eventToEdit != null) {
                Toast.makeText(getContext(), "Discover events cannot be edited", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }

            String name = eventName.getText().toString().trim();
            int startTimeMin = startHour.getValue() * 60 + (startMinute.getValue() * 5);
            int endTimeMin = endHour.getValue() * 60 + (endMinute.getValue() * 5);

            if (name.isEmpty()) {
                eventName.setError("Please enter event name");
                return;
            }

            if (endTimeMin <= startTimeMin) {
                Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                return;
            }

            String dateStr = selectedDate[0].toString();
            String startTimeStr = String.format(Locale.getDefault(), "%02d:%02d", startTimeMin / 60, startTimeMin % 60);
            String endTimeStr = String.format(Locale.getDefault(), "%02d:%02d", endTimeMin / 60, endTimeMin % 60);

            try {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                cal.set(selectedDate[0].getYear(), selectedDate[0].getMonthValue() - 1, selectedDate[0].getDayOfMonth());
                String[] startParts = startTimeStr.split(":");
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startParts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(startParts[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long startTimeMillis = cal.getTimeInMillis();

                cal.set(Calendar.HOUR_OF_DAY, endTimeMin / 60);
                cal.set(Calendar.MINUTE, endTimeMin % 60);
                long endTimeMillis = cal.getTimeInMillis();

                List<String> tags = new ArrayList<>();
                tags.add("personal");
                if (name.toLowerCase().contains("meeting")) tags.add("meeting");

                if (eventToEdit == null) {
                    Event newEvent = new Event(
                            name,
                            name + " scheduled via timetable",
                            "Not specified",
                            startTimeMillis,
                            endTimeMillis,
                            dateStr,
                            tags,
                            R.color.light_blue,
                            name,
                            "Scheduled Event",
                            R.drawable.default_event_image
                    );
                    UserRepository.addPersonalEventToUser(userEmail, newEvent);
                    eventList.add(newEvent);
                    Log.d(TAG, "Added event: " + name + ", date: " + dateStr);
                } else {
                    eventToEdit.setTitle(name);
                    eventToEdit.setDescription(name + " scheduled via timetable");
                    eventToEdit.setDate(dateStr);
                    eventToEdit.setStartTime(startTimeMillis);
                    eventToEdit.setEndTime(endTimeMillis);
                    UserRepository.removeUserEvent(userEmail, eventToEdit);
                    UserRepository.addPersonalEventToUser(userEmail, eventToEdit);
                    Log.d(TAG, "Updated event: " + name + ", date: " + dateStr);
                }

                timetableAdapter.setEventList(eventList);
                if (rowHeightPx > 0) {
                    eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                    eventCanvas.invalidate();
                }

                dialog.dismiss();
                Toast.makeText(getContext(), eventToEdit != null ? "Event updated!" : "Event saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error saving event: " + e.getMessage());
                Toast.makeText(getContext(), "Error saving event", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?" + (!isPersonalEvent ? " It will return to the Discover page." : ""))
                    .setPositiveButton("Delete", (d, which) -> {
                        if (eventToEdit != null) {
                            UserRepository.removeUserEvent(userEmail, eventToEdit);
                            eventList.remove(eventToEdit);
                            timetableAdapter.setEventList(eventList);
                            if (rowHeightPx > 0) {
                                eventCanvas.setData(eventList, currentWeekStart, rowHeightPx);
                                eventCanvas.invalidate();
                            }
                            Log.d(TAG, "Deleted event: " + eventToEdit.getTitle() + ", Tags: " + eventToEdit.getTags());
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