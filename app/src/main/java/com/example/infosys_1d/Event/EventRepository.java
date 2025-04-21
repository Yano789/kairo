package com.example.infosys_1d.Event;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.infosys_1d.R;
import com.example.infosys_1d.Login.UserRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

public class EventRepository {
    private static final String TAG = "EventRepository";
    private static List<Event> generalEvents = new ArrayList<>();
    private static Set<String> userAddedEventIds = new HashSet<>();
    private static final String PREF_NAME = "EventPrefs";

    public static void loadDummyEvents(Context context) {
        generalEvents.clear();
        userAddedEventIds.clear();
        loadUserAddedEventIds(context);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));

        // General Events
        cal.set(2025, Calendar.APRIL, 15, 10, 0);
        long startTime1 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 15, 16, 0);
        long endTime1 = cal.getTimeInMillis();
        Event techSummit = new Event(
                "Tech Summit 2025",
                "Latest innovations in AI and robotics",
                "SUTD Auditorium",
                startTime1,
                endTime1,
                "2025-04-15",
                Arrays.asList("general", "tech"),
                R.color.light_blue,
                "Tech Summit",
                "Innovation Showcase",
                R.drawable.tech_event
        );
        techSummit.setId("event_001");

        cal.set(2025, Calendar.APRIL, 16, 18, 0);
        long startTime2 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 16, 21, 0);
        long endTime2 = cal.getTimeInMillis();
        Event musicNight = new Event(
                "Music Night",
                "Live performances by student bands",
                "Campus Plaza",
                startTime2,
                endTime2,
                "2025-04-16",
                Arrays.asList("general", "music"),
                R.color.light_purple,
                "Music Night",
                "Live Concert",
                R.drawable.music_event
        );
        musicNight.setId("event_002");

        cal.set(2025, Calendar.APRIL, 18, 12, 0);
        long startTime3 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 18, 18, 0);
        long endTime3 = cal.getTimeInMillis();
        Event foodFestival = new Event(
                "Food Festival",
                "Taste cuisines from around the world",
                "East Coast Park",
                startTime3,
                endTime3,
                "2025-04-18",
                Arrays.asList("general", "food"),
                R.color.light_orange,
                "Food Festival",
                "Culinary Delight",
                R.drawable.food_event
        );
        foodFestival.setId("event_003");

        cal.set(2025, Calendar.APRIL, 19, 11, 0);
        long startTime4 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 19, 15, 0);
        long endTime4 = cal.getTimeInMillis();
        Event artExhibition = new Event(
                "Art Exhibition",
                "Showcase of student artwork",
                "SUTD Gallery",
                startTime4,
                endTime4,
                "2025-04-19",
                Arrays.asList("general", "art"),
                R.color.light_red,
                "Art Exhibition",
                "Creative Display",
                R.drawable.art_event
        );
        artExhibition.setId("event_004");

        // Fifthrow Events
        cal.set(2025, Calendar.APRIL, 14, 9, 0);
        long startTime5 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 14, 14, 0);
        long endTime5 = cal.getTimeInMillis();
        Event clubfair = new Event(
                "SUTD Clubfair",
                "Explore student clubs and societies",
                "Campus Courtyard",
                startTime5,
                endTime5,
                "2025-04-14",
                Arrays.asList("fifthrow", "club"),
                R.color.light_green,
                "Clubfair",
                "Student Activities",
                R.drawable.sutd_clubfair
        );
        clubfair.setId("event_005");

        cal.set(2025, Calendar.APRIL, 17, 7, 0);
        long startTime6 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 17, 10, 0);
        long endTime6 = cal.getTimeInMillis();
        Event charityRun = new Event(
                "Charity Run",
                "Run for a cause with Fifthrow",
                "Marina Bay",
                startTime6,
                endTime6,
                "2025-04-17",
                Arrays.asList("fifthrow", "charity"),
                R.color.light_yellow,
                "Charity Run",
                "Community Support",
                R.drawable.charity_event
        );
        charityRun.setId("event_006");

        cal.set(2025, Calendar.APRIL, 20, 13, 0);
        long startTime7 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 20, 15, 0);
        long endTime7 = cal.getTimeInMillis();
        Event fifthrowMeetup = new Event(
                "Fifthrow Meetup",
                "Networking with Fifthrow communities",
                "SUTD Lounge",
                startTime7,
                endTime7,
                "2025-04-20",
                Arrays.asList("fifthrow", "networking"),
                R.color.light_blue,
                "Fifthrow Meetup",
                "Community Event",
                R.drawable.sutd_clubfair
        );
        fifthrowMeetup.setId("event_007");

        cal.set(2025, Calendar.APRIL, 21, 14, 0);
        long startTime8 = cal.getTimeInMillis();
        cal.set(2025, Calendar.APRIL, 21, 17, 0);
        long endTime8 = cal.getTimeInMillis();
        Event volunteerDrive = new Event(
                "Volunteer Drive",
                "Join Fifthrow to volunteer",
                "Community Center",
                startTime8,
                endTime8,
                "2025-04-21",
                Arrays.asList("fifthrow", "volunteer"),
                R.color.light_green,
                "Volunteer Drive",
                "Give Back",
                R.drawable.charity_event
        );
        volunteerDrive.setId("event_008");

        generalEvents.add(techSummit);
        generalEvents.add(musicNight);
        generalEvents.add(foodFestival);
        generalEvents.add(artExhibition);
        generalEvents.add(clubfair);
        generalEvents.add(charityRun);
        generalEvents.add(fifthrowMeetup);
        generalEvents.add(volunteerDrive);

        Log.d(TAG, "Loaded " + generalEvents.size() + " dummy events");
        for (Event e : generalEvents) {
            Log.d(TAG, " - " + e.getName() + ", Date: " + e.getDate() + ", Tags: " + e.getTags() + ", Image: " + e.getImageResId());
        }
    }

    public static List<Event> getGeneralEvents() {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : generalEvents) {
            if (!userAddedEventIds.contains(event.getId())) {
                filteredEvents.add(event);
            }
        }
        Log.d(TAG, "Returning " + filteredEvents.size() + " general events (filtered)");
        return filteredEvents;
    }

    public static void moveToCalendar(String userEmail, Event event, Context context) {
        try {
            Event updatedEvent = new Event(
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
            updatedEvent.setId(event.getId());
            UserRepository.addPersonalEventToUser(userEmail, updatedEvent, context);
            userAddedEventIds.add(event.getId());
            saveUserAddedEventIds(context);
            Log.d(TAG, "Moved event '" + event.getName() + "' with ID " + event.getId() + " to calendar for " + userEmail);
        } catch (Exception e) {
            Log.e(TAG, "Failed to move event '" + event.getName() + "' to calendar for " + userEmail + ": " + e.getMessage());
        }
    }

    public static void removeFromCalendar(String userEmail, Event event, Context context) {
        try {
            UserRepository.removeUserEvent(userEmail, event, context);
            userAddedEventIds.remove(event.getId());
            saveUserAddedEventIds(context);
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

    public static void addPersonalEventToCalendar(String userEmail, Event event, Context context) {
        try {
            if (event.getId() == null || event.getId().isEmpty()) {
                event.setId("personal_" + UUID.randomUUID().toString());
            }
            Event updatedEvent = new Event(
                    event.getName(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getDate(),
                    new ArrayList<>(event.getTags()),
                    event.getColor() != 0 ? event.getColor() : R.color.light_blue,
                    event.getTitle(),
                    event.getSubtitle(),
                    event.getImageResId()
            );
            updatedEvent.setId(event.getId());
            UserRepository.addPersonalEventToUser(userEmail, updatedEvent, context);
            Log.d(TAG, "Added personal event '" + event.getName() + "' with ID " + event.getId() + " for " + userEmail);
        } catch (Exception e) {
            Log.e(TAG, "Failed to add personal event '" + event.getName() + "' for " + userEmail + ": " + e.getMessage());
        }
    }

    public static List<Event> getCalendarEvents(String userEmail) {
        List<Event> userEvents = UserRepository.getUserEvents(userEmail);
        Log.d(TAG, "Retrieved " + userEvents.size() + " calendar events for " + userEmail);
        return userEvents;
    }

    private static void saveUserAddedEventIds(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot save userAddedEventIds: Context is null");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("user_added_event_ids", userAddedEventIds);
        editor.apply();
        Log.d(TAG, "Saved " + userAddedEventIds.size() + " user-added event IDs");
    }

    private static void loadUserAddedEventIds(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot load userAddedEventIds: Context is null");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userAddedEventIds = new HashSet<>(prefs.getStringSet("user_added_event_ids", new HashSet<>()));
        Log.d(TAG, "Loaded " + userAddedEventIds.size() + " user-added event IDs");
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
    public static List<Event> findConflictingEvents(String userEmail, Event newEvent, Context context) {
        List<Event> conflictingEvents = new ArrayList<>();
        List<Event> userEvents = UserRepository.getUserEvents(userEmail);

        for (Event existingEvent : userEvents) {
            // Check for overlap: newEvent.start <= existingEvent.end && newEvent.end >= existingEvent.start
            if (newEvent.getStartTime() <= existingEvent.getEndTime() &&
                    newEvent.getEndTime() >= existingEvent.getStartTime()) {
                conflictingEvents.add(existingEvent);
            }
        }

        Log.d(TAG, "Found " + conflictingEvents.size() + " conflicting events for user " + userEmail);
        return conflictingEvents;
    }
}