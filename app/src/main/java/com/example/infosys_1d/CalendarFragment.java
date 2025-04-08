package com.example.infosys_1d;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Event.EventAdapter;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.Event.EventViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.infosys_1d.Calendar.MultiDotDecorator;

public class CalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter calendarAdapter;
    private EventViewModel eventViewModel;
    private final List<Event> calendarEvents = new ArrayList<>();
    private EventAdapter.OnEventActionListener onEventActionListener;
    private MaterialCalendarView calendarView;
    private TextView emptyView;

    private int visibleMonth;
    private int visibleYear;
    private int selectedYear;
    private int selectedMonth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class); // Shared ViewModel
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCalendar); // Make sure the ID is correct
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize empty view
        emptyView = view.findViewById(R.id.emptyViewCalendar); // Make sure this ID exists in the layout

        // Initialize calendar view
        calendarView = view.findViewById(R.id.materialCalendarView); // Make sure the ID is correct

        // Initialize the adapter if it's not initialized yet
        if (calendarAdapter == null) {
            calendarAdapter = new EventAdapter(requireContext(), new ArrayList<>(), R.layout.calendar_item_event, new EventAdapter.OnEventActionListener() {
                @Override
                public void onAddToCalendar(Event event) {
                    // Not applicable
                }

                @Override
                public void onRemoveFromCalendar(Event event) {
                    EventRepository.removeFromCalendar(event);
                    refreshEvents();
                }
            });
            recyclerView.setAdapter(calendarAdapter);
        }

        // Set the initial selected year and month
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);

        // Filter events based on the selected month
        filterEventsForMonth(selectedYear, selectedMonth);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupObservers();

        calendarView.setOnMonthChangedListener((widget, date) -> {
            visibleYear = date.getYear();
            visibleMonth = date.getMonth();
            filterEventsForMonth(visibleYear, visibleMonth);
            setupCalendarDecorator();
        });

        setupCalendarDecorator();
        setupCalendarListener();
    }

    private void setupRecyclerView() {
        calendarAdapter = new EventAdapter(requireContext(), calendarEvents, R.layout.calendar_item_event, new EventAdapter.OnEventActionListener() {
            @Override
            public void onAddToCalendar(Event event) {
                // Not applicable
            }

            @Override
            public void onRemoveFromCalendar(Event event) {
                EventRepository.removeFromCalendar(event);
                refreshEvents();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(calendarAdapter);
    }

    private void setupObservers() {
        eventViewModel.getCalendarEvents().observe(getViewLifecycleOwner(), events -> {
            calendarEvents.clear();
            calendarEvents.addAll(events);
            calendarAdapter.notifyDataSetChanged();
            updateEmptyView(events);
        });
    }

    public void refreshEvents() {
        eventViewModel.refreshEvents();
    }

    private void updateEmptyView(List<Event> events) {
        boolean isEmpty = events == null || events.isEmpty();

        // Check if emptyView and recyclerView are not null
        if (emptyView != null) {
            emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        }

        if (recyclerView != null) {
            recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void setupCalendarDecorator() {
        Map<CalendarDay, List<Integer>> eventColorMap = new HashMap<>();
        for (Event event : calendarEvents) {
            CalendarDay eventDay = null;
            long millis = event.getStartTime();
            org.threeten.bp.Instant instant = org.threeten.bp.Instant.ofEpochMilli(millis);
            org.threeten.bp.ZoneId zone = org.threeten.bp.ZoneId.systemDefault();
            org.threeten.bp.LocalDate date = instant.atZone(zone).toLocalDate();
            eventDay = CalendarDay.from(date);

            // Check for zero-based month and adjust if needed (if using 1-based month in your calendar)
            eventColorMap
                    .computeIfAbsent(eventDay, day -> new ArrayList<>())
                    .add(ContextCompat.getColor(requireContext(), R.color.light_red));
        }

        for (Map.Entry<CalendarDay, List<Integer>> entry : eventColorMap.entrySet()) {
            calendarView.addDecorator(new MultiDotDecorator(entry.getKey(), entry.getValue()));
        }
    }

    private void setupCalendarListener() {
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            filterEventsForDate(date);
        });
    }

    private void filterEventsForDate(CalendarDay date) {
        List<Event> filteredEvents = eventViewModel.getCalendarEvents().getValue().stream()
                .filter(event -> isSameDate(event.getStartTime(), date))
                .collect(Collectors.toList());

        if (filteredEvents.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            calendarAdapter.setEvents(filteredEvents);
            calendarAdapter.notifyDataSetChanged();
        }
    }

    private boolean isSameDate(long timestamp, CalendarDay date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        // Adjust for zero-based month (Calendar.MONTH is zero-based, so we need to check properly)
        return calendar.get(Calendar.YEAR) == date.getYear()
                && calendar.get(Calendar.MONTH) == (date.getMonth() - 1)  // Adjust for 1-based month in CalendarDay
                && calendar.get(Calendar.DAY_OF_MONTH) == date.getDay();
    }

    private void filterEventsForMonth(int year, int month) {
        // Get filtered events
        List<Event> filteredEvents = eventViewModel.getCalendarEvents().getValue().stream()
                .filter(e -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(e.getStartTime());

                    // Ensure you're adjusting for zero-based month
                    int eventMonth = cal.get(Calendar.MONTH);  // Zero-based month
                    return cal.get(Calendar.YEAR) == year && eventMonth == (month - 1);  // Adjust for 1-based month
                })
                .sorted(Comparator.comparingLong(e -> e.getStartTime()))
                .collect(Collectors.toList());

        // Ensure the adapter is initialized
        if (calendarAdapter == null) {
            calendarAdapter = new EventAdapter(requireContext(), new ArrayList<>(), R.layout.calendar_item_event, new EventAdapter.OnEventActionListener() {
                @Override
                public void onAddToCalendar(Event event) {
                    // Not applicable
                }

                @Override
                public void onRemoveFromCalendar(Event event) {
                    EventRepository.removeFromCalendar(event);
                    refreshEvents();
                }
            });
        }

        // Set the filtered events to the adapter
        calendarAdapter.setEvents(filteredEvents);
        calendarAdapter.notifyDataSetChanged();

        updateEmptyView(filteredEvents);  // Update the empty view
    }
}
