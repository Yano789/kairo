package com.example.infosys_1d;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/**
 * CalendarFragment displays a calendar with event indicators (dots)
 * and updates an event list in a RecyclerView.
 * It uses a dummy event database (to be replaced with a real database later).
 * It supports multiple events per day using Guava's Multimap and a custom MultiDotDrawable.
 */
public class CalendarFragment extends Fragment {
    private MaterialCalendarView calendarView; //UI Component for displaying the calendarI
    private EventAdapter eventAdapter;  // Adapter for displaying events in a RecyclerView
    private Multimap<String, Event> eventDatabase; // Dummy event database (allows multiple events per date)


    //Tracks the currently displayed month and year
    private int visibleMonth;
    private int visibleYear;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize UI Components
        calendarView = view.findViewById(R.id.materialCalendarView);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter with an empty list and set it on the RecyclerView
        eventAdapter = new EventAdapter(new ArrayList<>());
        recyclerView.setAdapter(eventAdapter);

        // Initialize the dummy event database using Guava's Multimap (allows duplicate keys)
        initializeEventDatabase();

        //Get current date to initialize visibleMonth and visibleYear
        CalendarDay current = calendarView.getCurrentDate();
        visibleMonth = current.getMonth();
        visibleYear = current.getYear();

        //Populate event list and update calendar indicators
        displayEventsForCurrentMonth();
        setCalendarEventIndicators();


        // Handles event selection on the calendar
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    date.getYear(), date.getMonth(), date.getDay());
            //If there are events on selected day, then update event list
            if (eventDatabase.containsKey(selectedDate)) {
                updateEventList(selectedDate);
            } else {
                displayEventsForCurrentMonth(); // fallback to full month if no event on selected day
            }
        });

        //Updates event list and dots whenever user changes the month
        calendarView.setOnMonthChangedListener((widget, date) -> {
            visibleMonth = date.getMonth();
            visibleYear = date.getYear();
            displayEventsForCurrentMonth();
            setCalendarEventIndicators();
        });


        return view;
    }

    // Displays dot indicators on the calendar for days that have events
    private void setCalendarEventIndicators() {
        int currentMonth = visibleMonth;
        int currentYear = visibleYear;

        //Clears existing event indicators before adding new ones
        calendarView.removeDecorators();

        //Map to store event colors for each day
        Map<CalendarDay, List<Integer>> dotsMap = new HashMap<>();

        //Loop through all stored event dates
        for (String dateStr : eventDatabase.keySet()) {
            String[] parts = dateStr.split("-");
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int d = Integer.parseInt(parts[2]);

            // Only show dots for the currently visible month
            if (y == currentYear && m == currentMonth) {
                CalendarDay dayKey = CalendarDay.from(y, m, d);
                //Get events for this date and extract their colors
                List<Event> events = new ArrayList<>(eventDatabase.get(dateStr));
                List<Integer> colors = new ArrayList<>();
                for (Event event : events) {
                    colors.add(ContextCompat.getColor(requireContext(), event.getColor()));
                }
                if (!dotsMap.containsKey(dayKey)){
                    dotsMap.put(dayKey, new ArrayList<>());
                }
                Objects.requireNonNull(dotsMap.get(dayKey)).addAll(colors);
            }
        }

        for (Map.Entry<CalendarDay, List<Integer>> entry : dotsMap.entrySet()) {
            calendarView.addDecorator(new MultiDotDecorator(entry.getKey(), entry.getValue()));
        }

        calendarView.invalidateDecorators();
    }


    // Initializes the dummy event database with hardcoded event data using Guava's Multimap.
    private void initializeEventDatabase() {
        // Create the Multimap instance to allow multiple events per date.
        eventDatabase = ArrayListMultimap.create();

        // March Events (Multiple events for 2025-03-06 are added under the same key)
        eventDatabase.put("2025-03-06", new Event("Hostel Event", "Pillows, Plushies, PJs", "Hostel Lounge",
                "19:00", "22:00", "2025-03-06", List.of("Fun", "Social"), R.color.light_blue));
        eventDatabase.put("2025-03-06", new Event("Hostel Event 2", "Pillows, Plushies, PJs", "Hostel Lounge",
                "18:00", "22:00", "2025-03-06", List.of("Fun", "Social"), R.color.light_red));
        eventDatabase.put("2025-03-07", new Event("ISTD Welcome Session", "Introduction to ISTD", "Auditorium",
                "13:00", "16:00", "2025-03-07", List.of("Academics", "Orientation"), R.color.light_green));
        eventDatabase.put("2025-03-15", new Event("Hackathon", "24-hour Coding Challenge", "Tech Hub",
                "09:00", "09:00", "2025-03-15", List.of("Coding", "Competition"), R.color.light_orange));
        eventDatabase.put("2025-03-22", new Event("SUTD Open House", "Explore SUTD Campus", "Main Campus",
                "10:30", "17:00", "2025-03-22", List.of("Education", "Campus"), R.color.light_red));
        eventDatabase.put("2025-03-23", new Event("Music Night", "Live Band Performance", "Student Center",
                "19:00", "21:00", "2025-03-23", List.of("Music", "Social"), R.color.light_red));
        eventDatabase.put("2025-03-27", new Event("SUTD Bands", "Picnic at the Disco II", "Outdoor Stage",
                "13:00", "16:00", "2025-03-27", List.of("Music", "Social"), R.color.light_orange));

        // April Events (5 Events)
        eventDatabase.put("2025-04-05", new Event("Startup Pitch", "Investor Demo Day", "Innovation Lab",
                "14:00", "18:00", "2025-04-05", List.of("Business", "Startup"), R.color.light_green));
        eventDatabase.put("2025-04-10", new Event("Tech Talk", "AI & the Future", "Hall A",
                "15:00", "17:00", "2025-04-10", List.of("Tech", "AI"), R.color.light_blue));
        eventDatabase.put("2025-04-14", new Event("Gaming Tournament", "Esports Battle", "Rec Center",
                "10:00", "20:00", "2025-04-14", List.of("Gaming", "Competition"), R.color.light_orange));
        eventDatabase.put("2025-04-20", new Event("Networking Night", "Meet Entrepreneurs", "Co-Working Space",
                "18:00", "21:00", "2025-04-20", List.of("Business", "Networking"), R.color.light_green));
        eventDatabase.put("2025-04-28", new Event("Health & Wellness", "Yoga & Meditation", "Outdoor Park",
                "07:00", "09:00", "2025-04-28", List.of("Health", "Wellness"), R.color.light_red));

        // May Events (6 Events)
        eventDatabase.put("2025-05-03", new Event("Art Showcase", "Student Art Exhibition", "Gallery",
                "12:00", "17:00", "2025-05-03", List.of("Art", "Creativity"), R.color.light_red));
        eventDatabase.put("2025-05-05", new Event("Design Showcase", "Student Innovations", "Exhibition Hall",
                "10:00", "16:00", "2025-05-05", List.of("Design", "Creativity"), R.color.light_blue));
        eventDatabase.put("2025-05-12", new Event("Film Screening", "Student Film Premieres", "Auditorium",
                "18:00", "21:00", "2025-05-12", List.of("Film", "Culture"), R.color.light_orange));
        eventDatabase.put("2025-05-18", new Event("Hackathon", "24-hour Coding Challenge", "Tech Hub",
                "09:00", "09:00", "2025-05-18", List.of("Coding", "Competition"), R.color.light_orange));
        eventDatabase.put("2025-05-25", new Event("Book Fair", "Meet Authors & Buy Books", "Library",
                "11:00", "15:00", "2025-05-25", List.of("Books", "Culture"), R.color.light_green));
        eventDatabase.put("2025-05-30", new Event("Photography Workshop", "Learn from Experts", "Photo Studio",
                "10:00", "13:00", "2025-05-30", List.of("Photography", "Workshop"), R.color.light_red));
    }


    private void updateEventList(String selectedDate) {
        List<Event> filteredEvents = new ArrayList<>();

        //If event exists for selected date, retrieve them
        if (eventDatabase.containsKey(selectedDate)) {
            filteredEvents.addAll(eventDatabase.get(selectedDate));
        }

        //Sort by time before displaying them
        filteredEvents.sort(Comparator.comparing(Event::getStartTime));
        eventAdapter.updateEvents(filteredEvents);
    }

    /**
     * Displays all events for the currently visible month in the RecyclerView.
     */
    private void displayEventsForCurrentMonth() {

        int currentMonth = visibleMonth;
        int currentYear = visibleYear;

        //List to store events for current month
        List<Event> filteredEvents = new ArrayList<>();

        for (String key : eventDatabase.keySet()) {
            String[] keyParts = key.split("-");
            int eventYear = Integer.parseInt(keyParts[0]);
            int eventMonth = Integer.parseInt(keyParts[1]);

            //If event matches visibleMonth, add it
            if (eventYear == currentYear && eventMonth == currentMonth) {
                filteredEvents.addAll(eventDatabase.get(key));
            }
        }

        filteredEvents.sort(Comparator.comparing(Event::getDate));
        eventAdapter.updateEvents(filteredEvents);
    }

    public static class MultiDotDecorator implements DayViewDecorator {
        private final CalendarDay day;
        private final List<Integer> colors;

        public MultiDotDecorator(CalendarDay day, List<Integer> colors) {
            this.day = day;
            this.colors = colors.size() > 3 ? colors.subList(0,3): colors; //Limit to three to prevent overcrowding
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return this.day.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new MultiDotSpan(6,colors)); //Add to indicate multiple events on this day

        }


    }
}
