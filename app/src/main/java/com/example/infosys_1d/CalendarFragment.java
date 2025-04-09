package com.example.infosys_1d;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Calendar.MultiDotDecorator;
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
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        // Load events immediately when fragment is created
        eventViewModel.refreshEvents();
    }


    private void resetToMonthView() {
        // Get current month from calendar view
        CalendarDay currentMonth = calendarView.getCurrentDate();
        visibleYear = currentMonth.getYear();
        visibleMonth = currentMonth.getMonth();

        // Filter events for current month
        filterEventsForMonth(visibleYear, visibleMonth);

        // Optional: Clear selection and show all events
        calendarView.clearSelection();

        // Optional feedback to user
        Toast.makeText(getContext(), "Showing all events for month", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Find the button and set click listener
        ImageButton resetButton = view.findViewById(R.id.btn_reset_view);
        resetButton.setOnClickListener(v -> resetToMonthView());
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewCalendar);
        emptyView = view.findViewById(R.id.emptyViewCalendar);
        calendarView = view.findViewById(R.id.materialCalendarView);

        // Setup RecyclerView with empty list initially
        setupRecyclerView();

        // Set the initial selected year and month
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        visibleYear = selectedYear;
        visibleMonth = selectedMonth;

        // Load events immediately
        loadInitialEvents();

        return view;
    }
    private void loadInitialEvents() {
        if (eventViewModel.getCalendarEvents().getValue() != null) {
            // If we already have events, filter them
            filterEventsForMonth(visibleYear, visibleMonth);
            setupCalendarDecorator();
        } else {
            // If not, trigger a refresh
            refreshAndUpdateUI();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize with current date
        CalendarDay today = CalendarDay.today();
        visibleYear = today.getYear();
        visibleMonth = today.getMonth();
        selectedYear = visibleYear;
        selectedMonth = visibleMonth;

        // Setup views
        setupRecyclerView();
        setupCalendarListener();

        // Force initial load
        eventViewModel.refreshEvents();

        // Setup observer
        setupObservers();

        // Select today by default
        calendarView.setSelectedDate(today);
        filterEventsForDate(today);
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
                // Refresh immediately
                refreshAndUpdateUI();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(calendarAdapter);
    }

    private void setupObservers() {
        // Only set up the observer if it's not already set up
        if (!eventViewModel.getCalendarEvents().hasObservers()) {
            eventViewModel.getCalendarEvents().observe(getViewLifecycleOwner(), events -> {
                calendarEvents.clear();
                calendarEvents.addAll(events);

                // Update the current view based on what's being shown
                if (calendarView.getSelectedDate() != null) {
                    filterEventsForDate(calendarView.getSelectedDate());
                } else {
                    filterEventsForMonth(visibleYear, visibleMonth);
                }

                setupCalendarDecorator();
                updateEmptyView(events);
            });
        }
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
        calendarView.removeDecorators();
        Map<CalendarDay, List<Integer>> eventColorMap = new HashMap<>();

        for (Event event : calendarEvents) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getStartTime());
            CalendarDay eventDay = CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

            eventColorMap.computeIfAbsent(eventDay, day -> new ArrayList<>())
                    .add(event.getColor());
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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.YEAR) == date.getYear()
                && cal.get(Calendar.MONTH) == date.getMonth() - 1  // Calendar.MONTH is 0-based
                && cal.get(Calendar.DAY_OF_MONTH) == date.getDay();
    }

    private void filterEventsForMonth(int year, int month) {
        List<Event> filteredEvents = eventViewModel.getCalendarEvents().getValue().stream()
                .filter(e -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(e.getStartTime());
                    return cal.get(Calendar.YEAR) == year
                            && cal.get(Calendar.MONTH) == (month - 1); // Adjust for 1-based month
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
                    refreshAndUpdateUI(); // Use the new method instead
                }
            });
        }

        // Set the filtered events to the adapter
        calendarAdapter.setEvents(filteredEvents);
        calendarAdapter.notifyDataSetChanged();

        updateEmptyView(filteredEvents);  // Update the empty view
    }
    private void refreshAndUpdateUI() {
        // Show loading state if needed
        eventViewModel.refreshEvents();

        // Observe the LiveData if not already observing
        if (!eventViewModel.getCalendarEvents().hasObservers()) {
            eventViewModel.getCalendarEvents().observe(getViewLifecycleOwner(), events -> {
                calendarEvents.clear();
                calendarEvents.addAll(events);

                // Update the current view
                if (calendarView.getSelectedDate() != null) {
                    filterEventsForDate(calendarView.getSelectedDate());
                } else {
                    filterEventsForMonth(visibleYear, visibleMonth);
                }

                setupCalendarDecorator();
                updateEmptyView(events);
            });
        }
    }
}
