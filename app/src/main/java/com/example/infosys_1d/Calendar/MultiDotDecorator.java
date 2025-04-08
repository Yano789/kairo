package com.example.infosys_1d.Calendar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

public class MultiDotDecorator implements DayViewDecorator {
    private final CalendarDay day;
    private final List<Integer> colors;

    public MultiDotDecorator(CalendarDay day, List<Integer> colors) {
        this.day = day;
        this.colors = colors.size() > 3 ? colors.subList(0, 3) : colors; //Limit to three to prevent overcrowding
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return this.day.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new MultiDotSpan(6, colors)); //Add to indicate multiple events on this day
    }
}
