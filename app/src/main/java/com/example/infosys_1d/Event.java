package com.example.infosys_1d;

import java.io.Serializable;
import java.util.Calendar;

public class Event implements Serializable {
    private Calendar date;
    private int startHour; // 24-hour format (e.g., 11 for 11 AM, 14 for 2 PM)
    private int durationHours;
    private String name;
    private String location;
    private String description;
    private int color;

    public Event(Calendar date, int startHour, int endHour, String name, String location, String description, int color) {
        this.date = date;
        this.startHour = startHour;
        this.durationHours = endHour - startHour; // Calculate duration
        this.name = name;
        this.location = location;
        this.description = description;
        this.color = color;
    }

    public Calendar getDate() {
        return date;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public String getText() {
        return name; // Display the name in the calendar
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }
}