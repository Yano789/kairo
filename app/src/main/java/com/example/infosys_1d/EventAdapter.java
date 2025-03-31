package com.example.infosys_1d;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventDay.setText(event.getDate().split("-")[2]); // Extracts day (DD) from date string
        holder.eventDayText.setText(getDayName(event.getDate())); // Converts to Mon, Tue, etc.
        holder.title.setText(event.getName()); // Set event title
        holder.description.setText(event.getDescription()); // Set event description
        holder.time.setText(event.getStartTime() + " - " + event.getEndTime());
        holder.time.setText(event.getLocation());

        // Convert list of tags to a comma-separated string
        holder.tags.setText(String.join(", ", event.getTags()));

        // Set the background color dynamically
        holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), event.getColor())
        );
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // Update event list dynamically
    public void updateEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tags;

        public CardView cardView;
        TextView title, description, time, eventDay, eventDayText;

        EventViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewEvent); //Find cardview that will display event
            tags = itemView.findViewById(R.id.tvTags);

            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            time = itemView.findViewById(R.id.tvTime);
            eventDay = itemView.findViewById(R.id.tvEventDay);
            eventDayText = itemView.findViewById(R.id.tvEventDayText);
        }
    }

    // Helper method to get day of the week from a date string
    private String getDayName(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            java.util.Date dateObj = sdf.parse(date);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH); // EEE -> Mon, Tue
            return dayFormat.format(dateObj);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
