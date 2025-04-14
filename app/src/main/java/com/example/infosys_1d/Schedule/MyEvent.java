package com.example.infosys_1d.Schedule;

import java.time.LocalDate;

public class MyEvent {
    private String title;
    private LocalDate date;
    private int startTime;
    private int endTime;

    public MyEvent(String title, LocalDate date, int startTime, int endTime) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
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
    public void setTitle(String title) {
        this.title = title;
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

