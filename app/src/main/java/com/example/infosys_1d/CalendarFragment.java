package com.example.infosys_1d;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragment extends Fragment {
    private MaterialCalendarView calendarView;
    private EventAdapter eventAdapter;
    private Map<String, List<Event>> eventDatabase;
    private RecyclerView recyclerView;
    private TextView emptyView;

    private int visibleMonth;
    private int visibleYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.materialCalendarView);
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        emptyView = view.findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(getContext(), new ArrayList<>(), R.layout.calendar_item_event);
        recyclerView.setAdapter(eventAdapter);

        initializeEventDatabase();

        CalendarDay current = calendarView.getCurrentDate();
        visibleMonth = current.getMonth();
        visibleYear = current.getYear();

        displayEventsForCurrentMonth();
        setCalendarEventIndicators();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = String.format("%04d-%02d-%02d", date.getYear(), date.getMonth(), date.getDay());
            if (eventDatabase.containsKey(selectedDate)) {
                updateEventList(selectedDate);
            } else {
                displayEventsForCurrentMonth();
            }
        });

        calendarView.setOnMonthChangedListener((widget, date) -> {
            visibleMonth = date.getMonth();
            visibleYear = date.getYear();
            displayEventsForCurrentMonth();
            setCalendarEventIndicators();
        });

        return view;
    }

    private void initializeEventDatabase() {
        EventRepository.loadDummyEvents(requireContext());
        eventDatabase = new HashMap<>();

        for (Event event : EventRepository.getEvents()) {
            String date = event.getDate();
            if (!eventDatabase.containsKey(date)) {
                eventDatabase.put(date, new ArrayList<>());
            }
            eventDatabase.get(date).add(event);
        }

        Log.d("CalendarFragment", "Loaded " + eventDatabase.size() + " event days.");
    }

    private void updateEventList(String selectedDate) {
        List<Event> filteredEvents = new ArrayList<>(eventDatabase.get(selectedDate));
        filteredEvents.sort(Comparator.comparing(Event::getStartTime));
        eventAdapter.updateEvents(filteredEvents);
        updateRecyclerViewVisibility(filteredEvents);
    }

    private void displayEventsForCurrentMonth() {
        List<Event> filteredEvents = new ArrayList<>();
        for (String key : eventDatabase.keySet()) {
            String[] parts = key.split("-");
            int eventYear = Integer.parseInt(parts[0]);
            int eventMonth = Integer.parseInt(parts[1]);

            if (eventYear == visibleYear && eventMonth == visibleMonth) {
                filteredEvents.addAll(eventDatabase.get(key));
            }
        }
        filteredEvents.sort(Comparator.comparing(Event::getDate));
        eventAdapter.updateEvents(filteredEvents);
        updateRecyclerViewVisibility(filteredEvents);
    }

    private void updateRecyclerViewVisibility(List<Event> events) {
        if (events.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void setCalendarEventIndicators() {
        calendarView.removeDecorators();
        Map<CalendarDay, List<Integer>> dotsMap = new HashMap<>();

        for (String dateStr : eventDatabase.keySet()) {
            String[] parts = dateStr.split("-");
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int d = Integer.parseInt(parts[2]);

            if (y == visibleYear && m == visibleMonth) {
                CalendarDay dayKey = CalendarDay.from(y, m, d);
                List<Event> events = eventDatabase.get(dateStr);
                List<Integer> colors = new ArrayList<>();
                for (Event event : events) {
                    colors.add(event.getColor());
                }
                dotsMap.put(dayKey, colors);
            }
        }

        for (Map.Entry<CalendarDay, List<Integer>> entry : dotsMap.entrySet()) {
            calendarView.addDecorator(new MultiDotDecorator(entry.getKey(), entry.getValue()));
        }

        calendarView.invalidateDecorators();
    }
}
