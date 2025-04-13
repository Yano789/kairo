package com.example.infosys_1d.Event;

import android.content.Context;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.infosys_1d.Login.Student;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static List<Event> generalEvents = new ArrayList<>();

    // Retrieve all general events (shared among all users)
    public static List<Event> getGeneralEvents() {
        return new ArrayList<>(generalEvents);
    }

    // Get combined events (general + user's personal events)
    public static List<Event> getCombinedEvents(String userEmail) {
        List<Event> combined = new ArrayList<>(generalEvents);
        combined.addAll(UserRepository.getUserEvents(userEmail));
        return combined;
    }

    // Add a personal event to a specific user
    public static void addPersonalEvent(String userEmail, Event event) {
        UserRepository.addPersonalEventToUser(userEmail, event);
    }

    // Move an event to user's personal calendar
    public static void moveToCalendar(String userEmail, Event event) {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.w(TAG, "Cannot move event to calendar: userEmail is empty");
            return;
        }
        Event personalEvent = new Event(
                event.getName(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getDate(),
                new ArrayList<>(event.getTags()),
                event.getColor(),
                event.getTitle(),
                event.getSubtitle(),
                event.getImageResId()
        );
        personalEvent.addTag("personal");
        UserRepository.addPersonalEventToUser(userEmail, personalEvent);
        Log.d(TAG, "Moved event '" + event.getName() + "' (title: " + event.getTitle() + ") to calendar for " + userEmail);
    }

    // Check if an event is personal (has "personal" tag)
    private static boolean isPersonalEvent(Event event) {
        return event.getTags() != null && event.getTags().contains("personal");
    }

    // Load initial dummy data
    public static void loadDummyEvents(Context context) {
        if (generalEvents.isEmpty()) {
            Log.d(TAG, "Loading dummy events...");
            generalEvents.clear();

            // Add general events (unchanged)
            generalEvents.add(new Event("Tech Conference 2025", "Annual technology conference", "Convention Center",
                    convertTimeToMillis("2025-04-01", "09:00 AM"),
                    convertTimeToMillis("2025-04-01", "06:00 PM"),
                    "2025-04-01",
                    new ArrayList<>(List.of("tech", "conference", "networking")), // Ensure mutable list
                    ContextCompat.getColor(context, R.color.light_red),
                    "Tech Conference", "Innovation Summit", R.drawable.tech_event));

            generalEvents.add(new Event("Community Charity Run", "5K run for local charity", "City Park",
                    convertTimeToMillis("2025-04-15", "08:00 AM"),
                    convertTimeToMillis("2025-04-15", "12:00 PM"),
                    "2025-04-15",
                    new ArrayList<>(List.of("sports", "charity", "community", "fifthrow")),
                    ContextCompat.getColor(context, R.color.light_green),
                    "Charity Run", "Support Local Causes", R.drawable.charity_event));

            generalEvents.add(new Event("Art Exhibition Opening", "Contemporary art showcase", "Modern Art Museum",
                    convertTimeToMillis("2025-05-02", "06:00 PM"),
                    convertTimeToMillis("2025-05-02", "09:00 PM"),
                    "2025-05-02",
                    new ArrayList<>(List.of("art", "culture", "exhibition", "fifthrow")),
                    ContextCompat.getColor(context, R.color.light_purple),
                    "Art Exhibition", "New Artists", R.drawable.art_event));

            generalEvents.add(new Event("Food Festival", "International cuisine fair", "Downtown Square",
                    convertTimeToMillis("2025-05-20", "11:00 AM"),
                    convertTimeToMillis("2025-05-22", "10:00 PM"),
                    "2025-05-20",
                    new ArrayList<>(List.of("food", "festival", "family")),
                    ContextCompat.getColor(context, R.color.light_orange),
                    "Food Festival", "Taste the World", R.drawable.food_event));

            generalEvents.add(new Event("Summer Music Festival", "Outdoor music event", "Riverside Park",
                    convertTimeToMillis("2025-06-15", "02:00 PM"),
                    convertTimeToMillis("2025-06-17", "11:00 PM"),
                    "2025-06-15",
                    new ArrayList<>(List.of("music", "summer", "festival")),
                    ContextCompat.getColor(context, R.color.light_blue),
                    "Music Fest", "Live Performances", R.drawable.music_event));

// Assign personal events using email to avoid index issues
            List<Student> students = UserRepository.getSampleStudents();
            Log.d(TAG, "Students found: " + students.size());
            if (!students.isEmpty()) {
                // Jafira
                Event e1 = new Event("Team Meeting", "Weekly project sync", "Office - Room 302",
                        convertTimeToMillis("2025-04-01", "09:30 AM"),
                        convertTimeToMillis("2025-04-01", "10:30 AM"),
                        "2025-04-01",
                        new ArrayList<>(List.of("work", "meeting", "team", "personal")),
                        ContextCompat.getColor(context, R.color.light_blue),
                        "Team Sync", "Project Updates", R.drawable.default_event_image);
                UserRepository.addPersonalEventToUser("jafira@mymail.sutd.edu.sg", e1);

                // Sharon
                Event e2 = new Event("Dentist Appointment", "Regular dental checkup", "City Dental Clinic",
                        convertTimeToMillis("2025-04-05", "03:00 PM"),
                        convertTimeToMillis("2025-04-05", "04:00 PM"),
                        "2025-04-05",
                        new ArrayList<>(List.of("health", "appointment", "personal")),
                        ContextCompat.getColor(context, R.color.light_orange),
                        "Dentist", "Checkup", R.drawable.default_event_image);
                UserRepository.addPersonalEventToUser("Sharon@mymail.sutd.edu.sg", e2);

                // Mariano
                Event e3 = new Event("Job Interview", "Software Engineer position", "Tech Corp HQ - Floor 15",
                        convertTimeToMillis("2025-04-08", "02:00 PM"),
                        convertTimeToMillis("2025-04-08", "03:30 PM"),
                        "2025-04-08",
                        new ArrayList<>(List.of("career", "interview", "important", "personal")),
                        ContextCompat.getColor(context, R.color.light_red),
                        "Interview", "Tech Corp", R.drawable.default_event_image);
                UserRepository.addPersonalEventToUser("mariano_perdices@mymail.sutd.edu.sg", e3);
            } else {
                Log.e(TAG, "No students found for personal events");
            }
        } else {
            Log.d(TAG, "General events already loaded, skipping...");
        }
    }

    // Helper method to convert date/time strings to milliseconds
    public static long convertTimeToMillis(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
        try {
            return sdf.parse(date + " " + time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Remove an event from user's personal events
    public static void removeFromCalendar(String userEmail, Event event) {
        UserRepository.removeUserEvent(userEmail, event);
    }

    // Add a personal event directly to calendar (user's personal events)
    public static void addPersonalEventToCalendar(String userEmail, Event event) {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.w(TAG, "Cannot add event to calendar: userEmail is empty");
            return;
        }
        UserRepository.addPersonalEventToUser(userEmail, event);
        Log.d(TAG, "Added event '" + event.getName() + "' (title: " + event.getTitle() + ") to calendar for " + userEmail);
    }

    // Update an event in user's personal events
    public static void updateCalendarEvent(String userEmail, Event updatedEvent) {
        List<Event> userEvents = UserRepository.getUserEvents(userEmail);
        for (int i = 0; i < userEvents.size(); i++) {
            Event event = userEvents.get(i);
            if (event.getId().equals(updatedEvent.getId())) {
                userEvents.set(i, updatedEvent); // Replace with updated event
                break;
            }
        }
        // Update UserRepository with modified list
        UserRepository.updateUserEvents(userEmail, userEvents);
    }

    public static List<Event> getCalendarEvents(String userEmail) {
        return UserRepository.getUserEvents(userEmail);
    }
}