package com.example.infosys_1d.Schedule;

import java.time.LocalDate;

public class DateHolder {
    private LocalDate Date;

    public DateHolder(LocalDate date) {
        this.Date = date;
    }

    public LocalDate getDate() {
        return Date;
    }

    public void setDate(LocalDate date) {
        this.Date = date;
    }
}
