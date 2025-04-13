package com.example.infosys_1d.Login;

import android.util.Log;
import com.example.infosys_1d.Event.Event;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
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
                student.addPersonalEvent(event);
                Log.d(TAG, "Added event '" + event.getName() + "' to " + userEmail);
                found = true;
                break;
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
                    Log.d(TAG, " - " + e.getName() + ", tags: " + e.getTags());
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
                student.removePersonalEvent(event);
                Log.d(TAG, "Removed event '" + event.getName() + "' from " + userEmail);
                break;
            }
        }
    }

    public static void updateUserEvents(String userEmail, List<Event> updatedEvents) {
        for (Student student : getSampleStudents()) {
            if (student.getEmail().equalsIgnoreCase(userEmail)) {
                student.setPersonalEvents(updatedEvents);
                Log.d(TAG, "Updated events for " + userEmail + ": " + updatedEvents.size() + " events");
                break;
            }
        }
    }
}