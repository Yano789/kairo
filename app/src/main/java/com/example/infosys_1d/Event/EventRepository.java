package com.example.infosys_1d.Event;

import android.content.Context;
import android.util.Log;

import com.example.infosys_1d.R;
import com.example.infosys_1d.Login.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static List<Event> generalEvents = new ArrayList<>();

    public static void loadDummyEvents(Context context) {
        if (!generalEvents.isEmpty()) return;

        List<String> tags1 = Arrays.asList("conference", "tech");
        List<String> tags2 = Arrays.asList("fifthrow", "charity");
        List<String> tags3 = Arrays.asList("workshop", "tech");

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
        cal.set(2025, Calendar.APRIL, 20, 9, 0);
        long startTime1 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 20, 17, 0);
        long endTime1 = cal.getTimeInMillis();

        cal.set(2025, Calendar.APRIL, 15, 7, 0);
        long startTime2 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 15, 10, 0);
        long endTime2 = cal.getTimeInMillis();

        cal.set(2025, Calendar.APRIL, 25, 13, 0);
        long startTime3 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 25, 15, 0);
        long endTime3 = cal.getTimeInMillis();

        Event event1 = new Event(
                "Tech Conference 2025",
                "Annual tech conference",
                "Convention Center",
                startTime1,
                endTime1,
                "2025-04-20",
                tags1,
                R.color.light_blue,
                "Tech Conference",
                "Innovation Summit",
                R.drawable.default_event_image
        );
        event1.setId("event_001");

        Event event2 = new Event(
                "Community Charity Run",
                "Fifthrow charity event",
                "East Coast Park",
                startTime2,
                endTime2,
                "2025-04-15",
                tags2,
                R.color.light_green, // Changed to pastel
                "Charity Run",
                "Community Event",
                R.drawable.default_event_image
        );
        event2.setId("event_002");

        Event event3 = new Event(
                "AI Workshop",
                "Hands-on AI session",
                "SUTD Campus",
                startTime3,
                endTime3,
                "2025-04-25",
                tags3,
                R.color.light_blue,
                "AI Workshop",
                "Tech Learning",
                R.drawable.default_event_image
        );
        event3.setId("event_003");

        generalEvents.add(event1);
        generalEvents.add(event2);
        generalEvents.add(event3);
        Log.d(TAG, "Loaded " + generalEvents.size() + " dummy events");
    }

    public static List<Event> getGeneralEvents() {
        return new ArrayList<>(generalEvents);
    }

    public static void moveToCalendar(String userEmail, Event event) {
        try {
            // Ensure pastel color
            Event updatedEvent = new Event(
                    event.getName(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getDate(),
                    event.getTags(),
                    getPastelColor(event.getColor()),
                    event.getTitle(),
                    event.getSubtitle(),
                    event.getImageResId()
            );
            updatedEvent.setId(event.getId());
            UserRepository.addPersonalEventToUser(userEmail, updatedEvent);
            Log.d(TAG, "Moved event '" + event.getName() + "' with ID " + event.getId() + " to calendar for " + userEmail + ", Color: " + updatedEvent.getColor());
        } catch (Exception e) {
            Log.e(TAG, "Failed to move event '" + event.getName() + "' to calendar for " + userEmail + ": " + e.getMessage());
        }
    }

    private static int getPastelColor(int originalColor) {
        return originalColor; // Keep pastel colors
    }

    public static void removeFromCalendar(String userEmail, Event event) {
        try {
            UserRepository.removeUserEvent(userEmail, event);
            Log.d(TAG, "Removed event '" + event.getName() + "' with ID " + event.getId() + " from calendar for " + userEmail);
        } catch (Exception e) {
            Log.e(TAG, "Failed to remove event '" + event.getName() + "' from calendar for " + userEmail + ": " + e.getMessage());
        }
    }

    public static void addGeneralEvent(Event event) {
        if (event.getId() == null || event.getId().isEmpty()) {
            event.setId("event_" + UUID.randomUUID().toString());
        }
        generalEvents.add(event);
        Log.d(TAG, "Added general event '" + event.getName() + "' with ID " + event.getId());
    }

    public static void addPersonalEventToCalendar(String userEmail, Event event) {
        try {
            if (event.getId() == null || event.getId().isEmpty()) {
                event.setId("personal_" + UUID.randomUUID().toString());
            }
            // Ensure pastel color
            Event updatedEvent = new Event(
                    event.getName(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getDate(),
                    event.getTags(),
                    getPastelColor(event.getColor()),
                    event.getTitle(),
                    event.getSubtitle(),
                    event.getImageResId()
            );
            updatedEvent.setId(event.getId());
            UserRepository.addPersonalEventToUser(userEmail, updatedEvent);
            Log.d(TAG, "Added personal event '" + event.getName() + "' with ID " + event.getId() + " for " + userEmail + ", Color: " + updatedEvent.getColor());
        } catch (Exception e) {
            Log.e(TAG, "Failed to add personal event '" + event.getName() + "' for " + userEmail + ": " + e.getMessage());
        }
    }

    public static List<Event> getCalendarEvents(String userEmail) {
        List<Event> userEvents = UserRepository.getUserEvents(userEmail);
        Log.d(TAG, "Retrieved " + userEvents.size() + " calendar events for " + userEmail);
        return userEvents;
    }

    public static String getDateString(String dateInput, String currentDate) {
        if (dateInput == null || dateInput.isEmpty()) {
            return currentDate;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        try {
            sdf.parse(dateInput);
            return dateInput;
        } catch (ParseException e) {
            // Handle relative dates
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            try {
                cal.setTime(sdf.parse(currentDate));
            } catch (ParseException ex) {
                Log.e(TAG, "Invalid current date: " + currentDate);
                return currentDate;
            }

            String lowerInput = dateInput.toLowerCase();
            if (lowerInput.contains("tomorrow")) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            } else if (lowerInput.contains("days from now")) {
                try {
                    int days = Integer.parseInt(lowerInput.replaceAll("[^0-9]", ""));
                    cal.add(Calendar.DAY_OF_MONTH, days);
                } catch (NumberFormatException ex) {
                    Log.w(TAG, "Could not parse days in: " + dateInput);
                    return currentDate;
                }
            } else if (lowerInput.contains("today")) {
                // Use current date
            } else {
                try {
                    SimpleDateFormat[] formats = {
                            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                            new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()),
                            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),
                            new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()),
                            new SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                    };
                    for (SimpleDateFormat format : formats) {
                        format.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                        try {
                            Date date = format.parse(dateInput);
                            return sdf.format(date);
                        } catch (ParseException ignored) {
                            // Try next format
                        }
                    }
                } catch (Exception ex) {
                    Log.w(TAG, "Unrecognized date format: " + dateInput);
                    return currentDate;
                }
            }
            return sdf.format(cal.getTime());
        }
    }

    public static long convertTimeToMillis(String dateStr, String timeStr) {
        try {
            SimpleDateFormat[] timeFormats = {
                    new SimpleDateFormat("h:mm a", Locale.getDefault()),
                    new SimpleDateFormat("h:mma", Locale.getDefault()),
                    new SimpleDateFormat("HH:mm", Locale.getDefault()),
                    new SimpleDateFormat("h a", Locale.getDefault()),
                    new SimpleDateFormat("ha", Locale.getDefault())
            };
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

            Date date = dateFormat.parse(dateStr);
            if (date == null) return 0;

            Calendar dateCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            dateCal.setTime(date);

            for (SimpleDateFormat timeFormat : timeFormats) {
                timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                try {
                    Date time = timeFormat.parse(timeStr);
                    if (time == null) continue;

                    Calendar timeCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
                    timeCal.setTime(time);

                    dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                    dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                    dateCal.set(Calendar.SECOND, 0);
                    dateCal.set(Calendar.MILLISECOND, 0);

                    return dateCal.getTimeInMillis();
                } catch (ParseException e) {
                    // Try next format
                }
            }
            Log.w(TAG, "Failed to parse time: " + timeStr);
            return 0;
        } catch (Exception e) {
            Log.e(TAG, "Error converting time to millis: date=" + dateStr + ", time=" + timeStr + ", error=" + e.getMessage());
            return 0;
        }
    }
}