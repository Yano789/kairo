package com.example.infosys_1d;

import java.util.List;

public class Event {
    private String name;
    private String description;

    private String location;
    private String startTime;
    private String endTime;
    private String date;
    private List<String> tags;

    private int color;

    public Event(String name, String description, String location, String startTime, String endTime, String date, List<String> tags, int color) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.tags = tags;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getDate() {
        return date;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getColor() {
        return color;
    }
}
