package com.example.infosys_1d.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.infosys_1d.AppContext;
import com.example.infosys_1d.Event.Event;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

            Student s1 = new Student(
                    "Computer Science and Design",
                    fifthRows,
                    "kairo123",
                    new BigInteger("1007916"),
                    "jafira@mymail.sutd.edu.sg",
                    "Jafira Nassar");
            Student s2 = new Student(
                    new BigInteger("1007915"),
                    "Sharon@mymail.sutd.edu.sg",
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

            sampleStudents.add(s1);
            sampleStudents.add(s2);
            sampleStudents.add(s3);
            Log.d(TAG, "Sample students initialized: " + sampleStudents.size());
        }
        return sampleStudents;
    }

    public static ArrayList<Admin> getSampleAdmins() {
        ArrayList<Admin> admins = new ArrayList<>();
        Admin fAdmin = new FacultyAdmin("admin1@school.edu", "Dennis Wasabi", new BigInteger("9001"), "adminpass");
        Admin frAdmin = new FifthRowAdmin("admin2@fifthrow.edu", "Shreya", new BigInteger("9002"), "fifthrowpass");
        admins.add(fAdmin);
        admins.add(frAdmin);
        return admins;
    }

    public static void addPersonalEventToUser(String userEmail, Event event) {
        boolean found = false;
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    student.addPersonalEvent(event);
                    saveEventsToPreferences(userEmail, student.getPersonalEvents());
                    Log.d(TAG, "Successfully added event '" + event.getName() + "' on " + event.getDate() + " to " + userEmail);
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
                    Log.d(TAG, " - " + e.getName() + ", date: " + e.getDate());
                }
                return events;
            }
        }
        Log.w(TAG, "No student found for email: " + userEmail);
        return new ArrayList<>();
    }

    public static void removeUserEvent(String userEmail, Event event) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    student.removePersonalEvent(event);
                    saveEventsToPreferences(userEmail, student.getPersonalEvents());
                    Log.d(TAG, "Successfully removed event '" + event.getName() + "' from " + userEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to remove event '" + event.getName() + "' from " + userEmail + ": " + e.getMessage());
                }
                break;
            }
        }
    }

    public static void updateUserEvents(String userEmail, List<Event> updatedEvents) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                try {
                    student.setPersonalEvents(updatedEvents);
                    saveEventsToPreferences(userEmail, updatedEvents);
                    Log.d(TAG, "Successfully updated events for " + userEmail + ": " + updatedEvents.size() + " events");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to update events for " + userEmail + ": " + e.getMessage());
                }
                break;
            }
        }
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
                    }
                    student.setPersonalEvents(events);
                    Log.d(TAG, "Loaded " + events.size() + " events for user: " + userEmail);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading events for " + userEmail + ": " + e.getMessage());
                }
            }
        }
    }

    private static void saveEventsToPreferences(String userEmail, List<Event> events) {
        Context context = AppContext.getAppContext();
        if (context == null) {
            Log.e(TAG, "Cannot save events: Context is null");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Event event : events) {
                JSONObject json = new JSONObject();
                json.put("id", event.getId());
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