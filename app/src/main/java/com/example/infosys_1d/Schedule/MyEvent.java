package com.example.infosys_1d.Schedule;

import java.time.LocalDate;

public class MyEvent {
    private String name;
    private LocalDate date;
    private int startTime;
    private int endTime;

    public MyEvent(String name, LocalDate date, int startTime, int endTime) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }
    public LocalDate getDate() {
        return date;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getEndTime() {
        return endTime;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }


}

