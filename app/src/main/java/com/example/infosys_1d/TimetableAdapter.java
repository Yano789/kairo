package com.example.infosys_1d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.MyEvent;

import java.time.LocalDate;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {
    private List<MyEvent> eventList;
    private final LocalDate currentWeekStart;

    public TimetableAdapter(Context context, List<MyEvent> eventList, LocalDate currentWeekStart) {
        this.eventList = eventList;
        this.currentWeekStart = currentWeekStart;
    }

    public void setEventList(List<MyEvent> newList) {
        this.eventList = newList;
        notifyDataSetChanged(); // Refresh UI after update
    }

    @NonNull
    @Override
    public TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timetable_row, parent, false);
        return new TimetableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimetableViewHolder holder, int position) {

    }


    private String formatTime(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        return String.format("%02d:%02d", hour, min);
    }

    @Override
    public int getItemCount() {
        return 288; // 24 hours * 12 slots per hour (5-min intervals)
    }
}


