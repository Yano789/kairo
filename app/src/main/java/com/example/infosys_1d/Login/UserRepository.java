package com.example.infosys_1d.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.infosys_1d.AppContext;
import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.Calendar;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static final String PREF_NAME = "UserEvents";
    private static ArrayList<Student> sampleStudents = null;

    public static ArrayList<Student> getSampleStudents() {
        if (sampleStudents == null) {
            Log.d(TAG, "Initializing sample students");
            sampleStudents = new ArrayList<>();
            ArrayList<FifthRowID> fifthRows = new ArrayList<>();
            ArrayList<FifthRowID> fifthRows1 = new ArrayList<>();
            ArrayList<FifthRowID> fifthRows2 = new ArrayList<>();
            fifthRows.add(new FifthRowID(new BigInteger("50"), "MindSports"));
            fifthRows2.add(new FifthRowID(new BigInteger("51"), "CAT"));

            List<Event> marianoEvents = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

            cal.set(2025, Calendar.APRIL, 14, 9, 0);
            long algoStart = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 14, 11, 0);
            long algoEnd = cal.getTimeInMillis();
            Event algo = new Event(
                    "Algorithms",
                    "Study of algorithms and complexity",
                    "Lecture Hall 1",
                    algoStart,
                    algoEnd,
                    "2025-04-14",
                    Arrays.asList("personal", "class"),
                    R.color.light_blue,
                    "Algorithms",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            algo.setId("personal_001");
            marianoEvents.add(algo);

            cal.set(2025, Calendar.APRIL, 14, 14, 0);
            long designStart = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 14, 16, 0);
            long designEnd = cal.getTimeInMillis();
            Event design = new Event(
                    "Design Studio",
                    "Project-based design work",
                    "Design Lab",
                    designStart,
                    designEnd,
                    "2025-04-14",
                    Arrays.asList("personal", "class"),
                    R.color.light_green,
                    "Design Studio",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            design.setId("personal_002");
            marianoEvents.add(design);

            cal.set(2025, Calendar.APRIL, 15, 10, 0);
            long softEngStart = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 15, 12, 0);
            long softEngEnd = cal.getTimeInMillis();
            Event softEng = new Event(
                    "Software Engineering",
                    "Software development methodologies",
                    "Lecture Hall 2",
                    softEngStart,
                    softEngEnd,
                    "2025-04-15",
                    Arrays.asList("personal", "class"),
                    R.color.light_purple,
                    "Software Engineering",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            softEng.setId("personal_003");
            marianoEvents.add(softEng);

            cal.set(2025, Calendar.APRIL, 16, 9, 0);
            long dataStart = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 16, 11, 0);
            long dataEnd = cal.getTimeInMillis();
            Event dataStruct = new Event(
                    "Data Structures",
                    "Advanced data structures",
                    "Lecture Hall 1",
                    dataStart,
                    dataEnd,
                    "2025-04-16",
                    Arrays.asList("personal", "class"),
                    R.color.light_blue,
                    "Data Structures",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            dataStruct.setId("personal_004");
            marianoEvents.add(dataStruct);

            cal.set(2025, Calendar.APRIL, 16, 13, 0);
            long aiStart = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 16, 15, 0);
            long aiEnd = cal.getTimeInMillis();
            Event aiLab = new Event(
                    "AI Lab",
                    "Practical AI applications",
                    "AI Lab",
                    aiStart,
                    aiEnd,
                    "2025-04-16",
                    Arrays.asList("personal", "class"),
                    R.color.light_red,
                    "AI Lab",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            aiLab.setId("personal_005");
            marianoEvents.add(aiLab);

            cal.set(2025, Calendar.APRIL, 17, 10, 0);
            long softEng2Start = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 17, 12, 0);
            long softEng2End = cal.getTimeInMillis();
            Event softEng2 = new Event(
                    "Software Engineering",
                    "Software development methodologies",
                    "Lecture Hall 2",
                    softEng2Start,
                    softEng2End,
                    "2025-04-17",
                    Arrays.asList("personal", "class"),
                    R.color.light_purple,
                    "Software Engineering",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            softEng2.setId("personal_006");
            marianoEvents.add(softEng2);

            cal.set(2025, Calendar.APRIL, 18, 14, 0);
            long design2Start = cal.getTimeInMillis();
            cal.set(2025, Calendar.APRIL, 18, 16, 0);
            long design2End = cal.getTimeInMillis();
            Event design2 = new Event(
                    "Design Studio",
                    "Project-based design work",
                    "Design Lab",
                    design2Start,
                    design2End,
                    "2025-04-18",
                    Arrays.asList("personal", "class"),
                    R.color.light_green,
                    "Design Studio",
                    "CSD Course",
                    R.drawable.default_event_image
            );
            design2.setId("personal_007");
            marianoEvents.add(design2);

            Student s1 = new Student(
                    "Computer Science and Design",
                    fifthRows,
                    "kairo123",
                    new BigInteger("1007916"),
                    "jafira@mymail.sutd.edu.sg",
                    "Jafira Nassar");
            Student s2 = new Student(
                    new BigInteger("1007915"),
                    "sharon@mymail.sutd.edu.sg",
                    "Sharon Ashok",
                    "Computer Science and Design",
                    "kairo123");
            Student s3 = new Student(
                    "Computer Science and Design",
                    fifthRows,
                    "kairo123",
                    new BigInteger("1008005"),
                    "mariano_perdices@mymail.sutd.edu.sg",
                    "Mariano Perdices");
            s3.setPersonalEvents(marianoEvents);

            sampleStudents.add(s1);
            sampleStudents.add(s2);
            sampleStudents.add(s3);
            Log.d(TAG, "Sample students initialized: " + sampleStudents.size());
            Log.d(TAG, "Mariano's events: " + marianoEvents.size());
        }
        return sampleStudents;
    }

    public static ArrayList<Admin> getSampleAdmins() {
        ArrayList<Admin> admins = new ArrayList<>();
        Admin volleyballAdmin = new FifthRowAdmin(
                "volleyball@club.sutd.edu.sg",
                "Volleyball Club",
                "F9003",
                "volley123"
        );
        Admin mindsportsAdmin = new FifthRowAdmin(
                "mindsports@club.sutd.edu.sg",
                "MindSports Club",
                "F9004",
                "minds123"
        );
        admins.add(volleyballAdmin);
        admins.add(mindsportsAdmin);
        Log.d(TAG, "Sample admins initialized: " + admins.size());
        return admins;
    }

    public static void addPersonalEventToUser(String userEmail, Event event, Context context) {
        boolean found = false;
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    // Ensure "personal" tag
                    List<String> tags = new ArrayList<>(event.getTags());
                    if (!tags.contains("personal")) {
                        tags.add("personal");
                        event.setTags(tags);
                        Log.d(TAG, "Added 'personal' tag to event: " + event.getName() + ", Date: " + event.getDate());
                    }
                    student.addPersonalEvent(event);
                    saveEventsToPreferences(userEmail, student.getPersonalEvents(), context);
                    Log.d(TAG, "Successfully added event '" + event.getName() + "' on " + event.getDate() +
                            " to " + userEmail + ", Tags: " + event.getTags());
                    found = true;
                    break;
                } catch (Exception e) {
                    Log.e(TAG, "Failed to add event '" + event.getName() + "' to " + userEmail + ": " + e.getMessage());
                }
            }
        }
        if (!found) {
            Log.w(TAG, "No student found for email: " + userEmail + " when adding event '" + event.getName() + "'");
        }
    }

    public static List<Event> getUserEvents(String userEmail) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                List<Event> events = student.getPersonalEvents();
                Log.d(TAG, "Events for " + userEmail + ": " + events.size() + " events found");
                for (Event e : events) {
                    Log.d(TAG, " - " + e.getName() + ", date: " + e.getDate() + ", tags: " + e.getTags());
                }
                return events;
            }
        }
        Log.w(TAG, "No student found for email: " + userEmail);
        return new ArrayList<>();
    }

    public static void removeUserEvent(String userEmail, Event event, Context context) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    student.removePersonalEvent(event);
                    saveEventsToPreferences(userEmail, student.getPersonalEvents(), context);
                    Log.d(TAG, "Successfully removed event '" + event.getName() + "' from " + userEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to remove event '" + event.getName() + "' from " + userEmail + ": " + e.getMessage());
                }
                break;
            }
        }
    }

    public static void updateUserEvents(String userEmail, List<Event> updatedEvents, Context context) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    student.setPersonalEvents(updatedEvents);
                    saveEventsToPreferences(userEmail, updatedEvents, context);
                    Log.d(TAG, "Successfully updated events for " + userEmail + ": " + updatedEvents.size() + " events");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update events for " + userEmail + ": " + e.getMessage());
                }
                break;
            }
        }
    }

    public static void initializeEvents(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot initialize events: Context is null");
            return;
        }
        loadEventsFromPreferences(context);
        Log.d(TAG, "Initialized events from SharedPreferences");
    }

    public static void loadEventsFromPreferences(Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot load events: Context is null");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        for (Student student : getSampleStudents()) {
            String userEmail = student.getEmail();
            String jsonStr = prefs.getString(userEmail, null);
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    List<Event> events = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject json = jsonArray.getJSONObject(i);
                            Event event = new Event(
                                    json.getString("name"),
                                    json.getString("description"),
                                    json.getString("location"),
                                    json.getLong("startTime"),
                                    json.getLong("endTime"),
                                    json.getString("date"),
                                    jsonArrayToList(json.getJSONArray("tags")),
                                    json.getInt("color"),
                                    json.getString("title"),
                                    json.getString("subtitle"),
                                    json.getInt("imageResId")
                            );
                            event.setId(json.getString("id"));
                            events.add(event);
                            Log.d(TAG, "Loaded event: " + event.getName() + ", ID: " + event.getId() + " for " + userEmail);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing event at index " + i + " for " + userEmail + ": " + e.getMessage());
                        }
                    }
                    student.setPersonalEvents(events);
                    Log.d(TAG, "Loaded " + events.size() + " events for user: " + userEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading events for " + userEmail + ": " + e.getMessage());
                }
            } else {
                Log.d(TAG, "No saved events found for user: " + userEmail);
            }
        }
    }

    private static void saveEventsToPreferences(String userEmail, List<Event> events, Context context) {
        if (context == null) {
            Log.e(TAG, "Cannot save events for " + userEmail + ": Context is null");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Event event : events) {
                JSONObject json = new JSONObject();
                json.put("id", event.getId() != null ? event.getId() : "");
                json.put("name", event.getName());
                json.put("description", event.getDescription());
                json.put("location", event.getLocation());
                json.put("startTime", event.getStartTime());
                json.put("endTime", event.getEndTime());
                json.put("date", event.getDate());
                json.put("tags", new JSONArray(event.getTags()));
                json.put("color", event.getColor());
                json.put("title", event.getTitle());
                json.put("subtitle", event.getSubtitle());
                json.put("imageResId", event.getImageResId());
                jsonArray.put(json);
            }
            editor.putString(userEmail, jsonArray.toString());
            editor.apply();
            Log.d(TAG, "Saved " + events.size() + " events for user: " + userEmail);
        } catch (Exception e) {
            Log.e(TAG, "Error saving events for " + userEmail + ": " + e.getMessage());
            editor.remove(userEmail).apply(); // Clear invalid data
        }
    }

    private static List<String> jsonArrayToList(JSONArray jsonArray) throws Exception {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }
        return list;
    }
}