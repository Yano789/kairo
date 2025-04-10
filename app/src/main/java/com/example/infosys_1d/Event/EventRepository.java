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
            // Clear existing events
            generalEvents.clear();
            calendarEvents.clear();

            // General events (will appear in HomeFragment)
            generalEvents.add(new Event("Tech Conference 2025", "Annual technology conference", "Convention Center",
                    convertTimeToMillis("2025-04-01", "09:00 AM"),
                    convertTimeToMillis("2025-04-01", "06:00 PM"),
                    "2025-04-01",
                    List.of("tech", "conference", "networking"),
                    ContextCompat.getColor(context, R.color.light_red),
                    "Tech Conference", "Innovation Summit", R.drawable.tech_event));

            generalEvents.add(new Event("Community Charity Run", "5K run for local charity", "City Park",
                    convertTimeToMillis("2025-04-15", "08:00 AM"),
                    convertTimeToMillis("2025-04-15", "12:00 PM"),
                    "2025-04-15",
                    List.of("sports", "charity", "community", "fifthrow"),
                    ContextCompat.getColor(context, R.color.light_green),
                    "Charity Run", "Support Local Causes", R.drawable.charity_event));

            generalEvents.add(new Event("Art Exhibition Opening", "Contemporary art showcase", "Modern Art Museum",
                    convertTimeToMillis("2025-05-02", "06:00 PM"),
                    convertTimeToMillis("2025-05-02", "09:00 PM"),
                    "2025-05-02",
                    List.of("art", "culture", "exhibition", "fifthrow"),
                    ContextCompat.getColor(context, R.color.light_purple),
                    "Art Exhibition", "New Artists", R.drawable.art_event));

            generalEvents.add(new Event("Food Festival", "International cuisine fair", "Downtown Square",
                    convertTimeToMillis("2025-05-20", "11:00 AM"),
                    convertTimeToMillis("2025-05-22", "10:00 PM"),
                    "2025-05-20",
                    List.of("food", "festival", "family"),
                    ContextCompat.getColor(context, R.color.light_orange),
                    "Food Festival", "Taste the World", R.drawable.food_event));

            generalEvents.add(new Event("Summer Music Festival", "Outdoor music event", "Riverside Park",
                    convertTimeToMillis("2025-06-15", "02:00 PM"),
                    convertTimeToMillis("2025-06-17", "11:00 PM"),
                    "2025-06-15",
                    List.of("music", "summer", "festival"),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Music Fest", "Live Performances", R.drawable.music_event));

            // Calendar events (will appear in CalendarFragment)
            calendarEvents.add(new Event("Team Meeting", "Weekly project sync", "Office - Room 302",
                    convertTimeToMillis("2025-04-01", "09:30 AM"),
                    convertTimeToMillis("2025-04-01", "10:30 AM"),
                    "2025-04-01",
                    List.of("work", "meeting", "team", "personal"),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Team Sync", "Project Updates", R.drawable.default_event_image));

            calendarEvents.add(new Event("Dentist Appointment", "Regular dental checkup", "City Dental Clinic",
                    convertTimeToMillis("2025-04-05", "03:00 PM"),
                    convertTimeToMillis("2025-04-05", "04:00 PM"),
                    "2025-04-05",
                    List.of("health", "appointment", "personal"),
                    ContextCompat.getColor(context, R.color.light_orange),
                    "Dentist", "Checkup"));

            calendarEvents.add(new Event("Job Interview", "Software Engineer position", "Tech Corp HQ - Floor 15",
                    convertTimeToMillis("2025-04-08", "02:00 PM"),
                    convertTimeToMillis("2025-04-08", "03:30 PM"),
                    "2025-04-08",
                    List.of("career", "interview", "important", "personal"),
                    ContextCompat.getColor(context, R.color.light_red),
                    "Interview", "Tech Corp"));

            calendarEvents.add(new Event("Friend's Birthday Party", "Birthday celebration", "123 Main St",
                    convertTimeToMillis("2025-04-12", "07:00 PM"),
                    convertTimeToMillis("2025-04-12", "11:30 PM"),
                    "2025-04-12",
                    List.of("social", "birthday", "friends", "personal"),
                    ContextCompat.getColor(context, R.color.light_green),
                    "Birthday", "Alex's Party"));

            calendarEvents.add(new Event("Car Service", "Regular maintenance", "AutoCare Center",
                    convertTimeToMillis("2025-04-18", "10:00 AM"),
                    convertTimeToMillis("2025-04-18", "12:00 PM"),
                    "2025-04-18",
                    List.of("car", "maintenance", "personal"),
                    ContextCompat.getColor(context, R.color.light_yellow),
                    "Car Service", "Oil Change"));

            calendarEvents.add(new Event("Parent-Teacher Conference", "School meeting", "Maplewood High School",
                    convertTimeToMillis("2025-04-22", "04:00 PM"),
                    convertTimeToMillis("2025-04-22", "05:00 PM"),
                    "2025-04-22",
                    List.of("family", "education", "personal"),
                    ContextCompat.getColor(context, R.color.light_purple),
                    "School Meeting", "Progress Report"));

            calendarEvents.add(new Event("Flight to New York", "Business trip", "International Airport",
                    convertTimeToMillis("2025-04-25", "06:00 AM"),
                    convertTimeToMillis("2025-04-25", "09:30 AM"),
                    "2025-04-25",
                    List.of("travel", "work", "personal"),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Flight", "JFK Airport"));

            calendarEvents.add(new Event("Anniversary Dinner", "Wedding anniversary", "La Bella Restaurant",
                    convertTimeToMillis("2025-04-30", "07:30 PM"),
                    convertTimeToMillis("2025-04-30", "10:00 PM"),
                    "2025-04-30",
                    List.of("personal", "anniversary", "dinner", "personal"),
                    ContextCompat.getColor(context, R.color.light_red),
                    "Anniversary", "5 Years"));

            // Adding some future events for variety
            calendarEvents.add(new Event("Conference Call", "Client project discussion", "Zoom",
                    convertTimeToMillis("2025-05-05", "11:00 AM"),
                    convertTimeToMillis("2025-05-05", "12:00 PM"),
                    "2025-05-05",
                    List.of("work", "meeting", "client", "personal"),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Client Call", "Project X"));

            calendarEvents.add(new Event("Vaccination Appointment", "Annual flu shot", "City Health Center",
                    convertTimeToMillis("2025-05-10", "09:00 AM"),
                    convertTimeToMillis("2025-05-10", "09:30 AM"),
                    "2025-05-10",
                    List.of("health", "prevention", "personal"),
                    ContextCompat.getColor(context, R.color.light_green),
                    "Vaccination", "Flu Shot"));
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