package com.example.infosys_1d.Login;

import com.example.infosys_1d.Event.Event;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String pillar;
    private List<FifthRowID> fifthRows;
    private String password;
    private BigInteger studentId;
    private String email;
    private String name;
    private List<Event> personalEvents;

    public Student(String pillar, List<FifthRowID> fifthRows, String password, BigInteger studentId, String email, String name) {
        this.pillar = pillar;
        this.fifthRows = fifthRows != null ? new ArrayList<>(fifthRows) : new ArrayList<>();
        this.password = password;
        this.studentId = studentId;
        this.email = email;
        this.name = name;
        this.personalEvents = new ArrayList<>();
    }

    public Student(BigInteger studentId, String email, String name, String pillar, String password) {
        this.studentId = studentId;
        this.email = email;
        this.name = name;
        this.pillar = pillar;
        this.password = password;
        this.fifthRows = new ArrayList<>();
        this.personalEvents = new ArrayList<>();
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public BigInteger getStudentId() { return studentId; }
    public String getPillar() { return pillar; }
    public String getPassword() { return password; }
    public List<FifthRowID> getFifthRows() { return new ArrayList<>(fifthRows); }
    public BigInteger getId() { return studentId; }
    public String getFacultyName() { return pillar != null ? pillar : ""; }

    public List<Event> getPersonalEvents() {
        return new ArrayList<>(personalEvents);
    }

    public void addPersonalEvent(Event event) {
        personalEvents.add(event);
    }

    public void removePersonalEvent(Event event) {
        personalEvents.removeIf(e -> e.getId().equals(event.getId()));
    }

    public void setPersonalEvents(List<Event> events) {
        this.personalEvents = new ArrayList<>(events);
    }
}