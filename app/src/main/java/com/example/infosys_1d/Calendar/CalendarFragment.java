package com.example.infosys_1d.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Event.EventAdapter;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.Login.UserRepository;
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
        currentUserEmail = getCurrentUserEmail();
        Log.d(TAG, "onCreate: currentUserEmail = " + currentUserEmail);
        if (currentUserEmail.isEmpty()) {
            Log.w(TAG, "No user email found, cannot load events");
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
        selectedMonth = calendar.get(Calendar.MONTH) + 1; // 1-based
        visibleYear = selectedYear;
        visibleMonth = selectedMonth;
        loadInitialEvents();
        return view;
    }

    private void loadInitialEvents() {
        if (!currentUserEmail.isEmpty()) {
            List<Event> userEvents = UserRepository.getUserEvents(currentUserEmail);
            calendarEvents.clear();
            calendarEvents.addAll(userEvents);
            filterEventsForMonth(visibleYear, visibleMonth);
            setupCalendarDecorator();
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
        if (!currentUserEmail.isEmpty()) {
            filterEventsForDate(today);
        }
        calendarView.setSelectedDate(today);
    }

    private void setupRecyclerView() {
        calendarAdapter = new EventAdapter(requireContext(), calendarEvents, R.layout.calendar_item_event,
                new EventAdapter.OnEventActionListener() {
                    @Override
                    public void onAddToCalendar(Event event) {
                        Log.d(TAG, "Add to calendar not applicable for: " + event.getName());
                    }

                    @Override
                    public void onRemoveFromCalendar(Event event) {
                        Log.d(TAG, "Removing event: " + event.getName() + ", ID: " + event.getId());
                        EventRepository.removeFromCalendar(currentUserEmail, event, requireContext());
                        loadInitialEvents();
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(calendarAdapter);
    }

    private void updateEmptyView(List<Event> events) {
        boolean isEmpty = events == null || events.isEmpty();
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void setupCalendarDecorator() {
        calendarView.removeDecorators();
        Map<CalendarDay, List<Integer>> eventColorMap = new HashMap<>();
        for (Event event : calendarEvents) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getStartTime());
            CalendarDay eventDay = CalendarDay.from(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            try {
                int color = ContextCompat.getColor(requireContext(), event.getColor());
                eventColorMap.computeIfAbsent(eventDay, day -> new ArrayList<>())
                        .add(color);
            } catch (Exception e) {
                Log.e(TAG, "Failed to resolve color for event: " + event.getName() + ", color: " + event.getColor());
            }
        }
        for (Map.Entry<CalendarDay, List<Integer>> entry : eventColorMap.entrySet()) {
            calendarView.addDecorator(new MultiDotDecorator(entry.getKey(), entry.getValue()));
        }
        Log.d(TAG, "Decorators added for " + eventColorMap.size() + " days");
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
        List<Event> userEvents = UserRepository.getUserEvents(currentUserEmail);
        calendarEvents.clear();
        calendarEvents.addAll(userEvents);
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
            try {
                int color = ContextCompat.getColor(requireContext(), e.getColor());
                Log.d(TAG, " - Filtered event: " + e.getName() + ", Color: 0x" + Integer.toHexString(color));
            } catch (Exception ex) {
                Log.e(TAG, "Color error for event: " + e.getName() + ", color: " + e.getColor());
            }
        }
        updateEventDisplay(filteredEvents);
    }

    private void filterEventsForMonth(int year, int month) {
        List<Event> userEvents = UserRepository.getUserEvents(currentUserEmail);
        calendarEvents.clear();
        calendarEvents.addAll(userEvents);
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
            try {
                int color = ContextCompat.getColor(requireContext(), e.getColor());
                Log.d(TAG, " - Filtered event: " + e.getName() + ", Color: 0x" + Integer.toHexString(color));
            } catch (Exception ex) {
                Log.e(TAG, "Color error for event: " + e.getName() + ", color: " + e.getColor());
            }
        }
        updateEventDisplay(filteredEvents);
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
        calendarAdapter.setEvents(events);
        updateEmptyView(events);
    }

    private String getCurrentUserEmail() {
        String email = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("user_email", "");
        Log.d(TAG, "getCurrentUserEmail: " + email);
        return email;
    }
}