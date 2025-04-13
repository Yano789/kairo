package com.example.infosys_1d.Calendar;

import android.content.Context;
import android.util.Log;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Event.EventAdapter;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.Event.EventViewModel;
import com.example.infosys_1d.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CalendarFragment extends Fragment {
    private static final String TAG = "CalendarFragment";

    private RecyclerView recyclerView;
    private EventAdapter calendarAdapter;
    private EventViewModel eventViewModel;
    private final List<Event> calendarEvents = new ArrayList<>();
    private MaterialCalendarView calendarView;
    private TextView emptyView;

    private int visibleMonth;
    private int visibleYear;
    private int selectedYear;
    private int selectedMonth;
    private String currentUserEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        currentUserEmail = getCurrentUserEmail();
        Log.d(TAG, "onCreate: currentUserEmail = " + currentUserEmail);
        if (currentUserEmail.isEmpty()) {
            Log.w(TAG, "No user email found, cannot load events");
            Toast.makeText(requireContext(), "Please log in to view your calendar", Toast.LENGTH_SHORT).show();
        } else {
            eventViewModel.refreshEvents(currentUserEmail);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ImageButton resetButton = view.findViewById(R.id.btn_reset_view);
        resetButton.setOnClickListener(v -> resetToMonthView());
        recyclerView = view.findViewById(R.id.recyclerViewCalendar);
        emptyView = view.findViewById(R.id.emptyViewCalendar);
        calendarView = view.findViewById(R.id.materialCalendarView);
        setupRecyclerView();
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        visibleYear = selectedYear;
        visibleMonth = selectedMonth;
        loadInitialEvents();
        return view;
    }

    private void loadInitialEvents() {
        if (!currentUserEmail.isEmpty()) {
            eventViewModel.refreshEvents(currentUserEmail);
            if (eventViewModel.getCalendarEvents().getValue() != null) {
                filterEventsForMonth(visibleYear, visibleMonth);
                setupCalendarDecorator();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CalendarDay today = CalendarDay.today();
        visibleYear = today.getYear();
        visibleMonth = today.getMonth();
        selectedYear = visibleYear;
        selectedMonth = visibleMonth;
        setupCalendarListener();
        setupObservers();
        if (!currentUserEmail.isEmpty()) {
            eventViewModel.refreshEvents(currentUserEmail);
        }
        calendarView.setSelectedDate(today);
        filterEventsForDate(today);
    }

    private void setupRecyclerView() {
        calendarAdapter = new EventAdapter(requireContext(), calendarEvents, R.layout.calendar_item_event,
                new EventAdapter.OnEventActionListener() {
                    @Override
                    public void onAddToCalendar(Event event) {
                        // Not applicable
                    }

                    @Override
                    public void onRemoveFromCalendar(Event event) {
                        EventRepository.removeFromCalendar(currentUserEmail, event);
                        refreshAndUpdateUI();
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(calendarAdapter);
    }

    private void setupObservers() {
        eventViewModel.getCalendarEvents().observe(getViewLifecycleOwner(), events -> {
            calendarEvents.clear();
            Log.d(TAG, "Received events for " + currentUserEmail + ": " + (events != null ? events.size() : 0));
            if (events != null) {
                for (Event event : events) {
                    Log.d(TAG, "Event: " + event.getName() + ", title: " + event.getTitle() + ", tags: " + event.getTags());
                    calendarEvents.add(event);
                }
            }
            Log.d(TAG, "Total events after processing: " + calendarEvents.size());
            if (calendarView.getSelectedDate() != null) {
                filterEventsForDate(calendarView.getSelectedDate());
            } else {
                filterEventsForMonth(visibleYear, visibleMonth);
            }
            setupCalendarDecorator();
            updateEmptyView(calendarEvents);
        });
    }

    private void updateEmptyView(List<Event> events) {
        boolean isEmpty = events == null || events.isEmpty();
        if (emptyView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void setupCalendarDecorator() {
        calendarView.removeDecorators();
        Map<CalendarDay, List<Integer>> eventColorMap = new HashMap<>();
        for (Event event : calendarEvents) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getStartTime());
            CalendarDay eventDay = CalendarDay.from(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            int color = modifyColorForPersonal(event.getColor());
            eventColorMap.computeIfAbsent(eventDay, day -> new ArrayList<>())
                    .add(color);
        }
        for (Map.Entry<CalendarDay, List<Integer>> entry : eventColorMap.entrySet()) {
            calendarView.addDecorator(new MultiDotDecorator(entry.getKey(), entry.getValue()));
        }
        Log.d(TAG, "Decorators added for " + eventColorMap.size() + " days");
    }

    private int modifyColorForPersonal(int originalColor) {
        return Color.argb(200,
                Color.red(originalColor),
                Color.green(originalColor),
                Color.blue(originalColor));
    }

    private void setupCalendarListener() {
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            filterEventsForDate(date);
        });
    }

    private void filterEventsForDate(CalendarDay date) {
        if (date != null) {
            filterEventsForDate(date.getYear(), date.getMonth(), date.getDay());
        }
    }

    private void filterEventsForDate(int year, int month, int day) {
        List<Event> filteredEvents = calendarEvents.stream()
                .filter(e -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(e.getStartTime());
                    return cal.get(Calendar.YEAR) == year
                            && cal.get(Calendar.MONTH) == (month - 1)
                            && cal.get(Calendar.DAY_OF_MONTH) == day;
                })
                .sorted(Comparator.comparingLong(Event::getStartTime))
                .collect(Collectors.toList());

        Log.d(TAG, "Filtered events for " + year + "-" + month + "-" + day + ": " + filteredEvents.size());
        for (Event e : filteredEvents) {
            Log.d(TAG, " - Filtered event: " + e.getName() + ", title: " + e.getTitle());
        }
        updateEventDisplay(filteredEvents);
    }

    private void filterEventsForMonth(int year, int month) {
        List<Event> filteredEvents = calendarEvents.stream()
                .filter(e -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(e.getStartTime());
                    return cal.get(Calendar.YEAR) == year
                            && cal.get(Calendar.MONTH) == (month - 1);
                })
                .sorted(Comparator.comparingLong(Event::getStartTime))
                .collect(Collectors.toList());

        Log.d(TAG, "Filtered events for " + year + "-" + month + ": " + filteredEvents.size());
        for (Event e : filteredEvents) {
            Log.d(TAG, " - Filtered event: " + e.getName() + ", title: " + e.getTitle());
        }
        updateEventDisplay(filteredEvents);
    }

    private void refreshAndUpdateUI() {
        if (!currentUserEmail.isEmpty()) {
            eventViewModel.refreshEvents(currentUserEmail);
        }
    }

    private void resetToMonthView() {
        CalendarDay currentMonth = calendarView.getCurrentDate();
        visibleYear = currentMonth.getYear();
        visibleMonth = currentMonth.getMonth();
        filterEventsForMonth(visibleYear, visibleMonth);
        calendarView.clearSelection();
        Toast.makeText(getContext(), "Showing all events for month", Toast.LENGTH_SHORT).show();
    }

    private void updateEventDisplay(List<Event> events) {
        if (calendarAdapter == null) {
            calendarAdapter = new EventAdapter(requireContext(), new ArrayList<>(),
                    R.layout.calendar_item_event, new EventAdapter.OnEventActionListener() {
                @Override
                public void onAddToCalendar(Event event) {
                    // Not applicable
                }

                @Override
                public void onRemoveFromCalendar(Event event) {
                    EventRepository.removeFromCalendar(currentUserEmail, event);
                    refreshAndUpdateUI();
                }
            });
            recyclerView.setAdapter(calendarAdapter);
        }

        calendarAdapter.setEvents(events);
        calendarAdapter.notifyDataSetChanged();
        updateEmptyView(events);
    }

    private String getCurrentUserEmail() {
        String email = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("user_email", "");
        Log.d(TAG, "getCurrentUserEmail: " + email);
        return email;
    }
}