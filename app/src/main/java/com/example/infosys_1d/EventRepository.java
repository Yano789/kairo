package com.example.infosys_1d;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static List<Event> events = new ArrayList<>();

    // Retrieve all events
    public static List<Event> getEvents() {
        return new ArrayList<>(events);  // Return a copy to avoid external modifications
    }

    // Add an event with full information
    public static void addEvent(String name, String description, String location, String startTime, String endTime, String date, List<String> tags, int color, String title, String subtitle, int imageResId) {
        Event event = new Event(name, description, location, startTime, endTime, date, tags, color, title, subtitle, imageResId);
        events.add(event);
    }

    // Add an event without an image
    public static void addEvent(String name, String description, String location, String startTime, String endTime, String date, List<String> tags, int color, String title, String subtitle) {
        Event event = new Event(name, description, location, startTime, endTime, date, tags, color, title, subtitle);
        events.add(event);
    }

    // Clear all events (for resetting or testing)
    public static void clearEvents() {
        events.clear();
    }

    // Load dummy events (only if empty)
    public static void loadDummyEvents(Context context) {
        if (events.isEmpty()) {
            events.add(new Event("Event 1", "Description 1", "Location 1", "12:00 PM", "2:00 PM", "2025-04-01", List.of("tag1", "tag2"), ContextCompat.getColor(context, R.color.light_red), "Event 1", "Subtitle 1", R.drawable.default_event_image));
            events.add(new Event("Event 2", "Description 2", "Location 2", "3:00 PM", "5:00 PM", "2025-04-02", List.of("tag2", "tag3"), ContextCompat.getColor(context, R.color.light_green), "Event 2", "Subtitle 2"));
            events.add(new Event("Event 3", "Description 3", "Location 3", "7:00 PM", "9:00 PM", "2025-04-03", List.of("tag1", "tag3", "fifthrow"), ContextCompat.getColor(context, R.color.light_blue), "Event 3", "Subtitle 3", R.drawable.default_event_image));
        }
    }

    // Placeholder for persistence
    public static void saveEventsToPreferences() {
        // Implement saving to SharedPreferences or database
    }
}
