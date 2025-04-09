package com.example.infosys_1d.Event;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.example.infosys_1d.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.time.Instant;
import java.util.Locale;

public class EventRepository {
    private static List<Event> generalEvents = new ArrayList<>();
    private static List<Event> calendarEvents = new ArrayList<>();

    // Retrieve all general events (for HomeFragment)
    public static List<Event> getGeneralEvents() {
        return new ArrayList<>(generalEvents);
    }

    // Retrieve all calendar events (for CalendarFragment)
    public static List<Event> getCalendarEvents() {
        return new ArrayList<>(calendarEvents);
    }

    // Move an event from general to calendar
    public static void moveToCalendar(Event event) {
        if (generalEvents.remove(event)) {
            calendarEvents.add(event);
        }
    }

    // Add a method to check if an event is personal
    private static boolean isPersonalEvent(Event event) {
        // Check if event has "personal" tag or other indicator
        return event.getTags() != null && event.getTags().contains("personal");
    }

    // Load dummy data into both lists
    public static void loadDummyEvents(Context context) {
        if (generalEvents.isEmpty() && calendarEvents.isEmpty()) {
            // General events (will appear in HomeFragment)
            generalEvents.clear();
            calendarEvents.clear();
            generalEvents.add(new Event("General Event 1", "Description 1", "Location 1",
                    convertTimeToMillis("2025-04-10", "10:00 AM"),
                    convertTimeToMillis("2025-04-10", "11:00 AM"),
                    "2025-04-10",
                    List.of("tag1", "tag2"),
                    ContextCompat.getColor(context, R.color.light_red),
                    "General Event 1", "Subtitle 1", R.drawable.default_event_image));

            generalEvents.add(new Event("General Event 2", "Description 2", "Location 2",
                    convertTimeToMillis("2025-04-15", "2:00 PM"),
                    convertTimeToMillis("2025-04-15", "4:00 PM"),
                    "2025-04-15",
                    List.of("fifthrow", "tag3"),
                    ContextCompat.getColor(context, R.color.light_green),
                    "General Event 2", "Subtitle 2"));

            // Calendar events (will appear in CalendarFragment)
            calendarEvents.add(new Event("Scheduled Event 1", "Important meeting", "Conference Room",
                    convertTimeToMillis("2025-04-01", "9:00 AM"),
                    convertTimeToMillis("2025-04-01", "10:00 AM"),
                    "2025-04-01",
                    List.of("meeting", "work", "personal"),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Meeting", "Team sync", R.drawable.default_event_image));

            calendarEvents.add(new Event("Scheduled Event 2", "Doctor appointment", "Clinic",
                    convertTimeToMillis("2025-04-05", "3:00 PM"),
                    convertTimeToMillis("2025-04-05", "4:00 PM"),
                    "2025-04-05",
                    List.of("health", "personal"),
                    ContextCompat.getColor(context, R.color.light_orange),
                    "Appointment", "Annual checkup"));
        }
    }

    // Modified removeFromCalendar to handle personal events differently
    public static void removeFromCalendar(Event event) {
        if (calendarEvents.remove(event)) {
            // Only add back to general events if it's not a personal event
            if (!isPersonalEvent(event)) {
                generalEvents.add(event);
            }
        }
    }

    public static long convertTimeToMillis(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            return sdf.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<Event> getAllEvents() {
        return new ArrayList<>(generalEvents);
    }

    public static void addPersonalEventToCalendar(Event event) {
        calendarEvents.add(event);
        // Ensure it's not in general events
        generalEvents.remove(event);
    }

    public static void updateCalendarEvent(Event updatedEvent) {
        for (Event event : calendarEvents) {
            if (event == updatedEvent) { // Same object reference
                // Update fields directly
                event.setTitle(updatedEvent.getTitle());
                event.setDescription(updatedEvent.getDescription());
                event.setLocation(updatedEvent.getLocation());
                break;
            }
        }
    }
}