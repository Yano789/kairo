package com.example.infosys_1d.Schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.R;

import java.time.LocalDate;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableViewHolder> {
    private List<Event> eventList;
    private LocalDate currentWeekStart;

    public TimetableAdapter(Context context, List<Event> eventList, LocalDate currentWeekStart) {
        this.eventList = eventList;
        this.currentWeekStart = currentWeekStart;
    }

    public void setEventList(List<Event> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
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
        // No event binding needed here; EventCanvasView handles rendering
    }

    public void setCurrentWeekStart(LocalDate currentWeekStart) {
        this.currentWeekStart = currentWeekStart;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 288; // 24 hours * 12 slots per hour (5-min intervals)
    }
}