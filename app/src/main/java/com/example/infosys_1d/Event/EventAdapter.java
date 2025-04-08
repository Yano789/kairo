package com.example.infosys_1d.Event;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CALENDAR = 1;
    private static final int TYPE_DISCOVERY = 2;

    private List<Event> eventList;
    private Context context;
    private int layoutResId;
    private OnEventActionListener actionListener;

    public interface OnEventActionListener {
        void onAddToCalendar(Event event);
        void onRemoveFromCalendar(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, int layoutResId, OnEventActionListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.layoutResId = layoutResId;
        this.actionListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutResId == R.layout.calendar_item_event ? TYPE_CALENDAR : TYPE_DISCOVERY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CALENDAR) {
            View view = inflater.inflate(R.layout.calendar_item_event, parent, false);
            return new CalendarViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.discovery_item_event, parent, false);
            return new DiscoveryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Event event = eventList.get(position);

        if (holder.getItemViewType() == TYPE_CALENDAR) {
            bindCalendarViewHolder((CalendarViewHolder) holder, event);
        } else {
            bindDiscoveryViewHolder((DiscoveryViewHolder) holder, event);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", event);
            context.startActivity(intent);
        });
    }

    private void bindCalendarViewHolder(CalendarViewHolder holder, Event event) {
        // Set date components
        String[] dateParts = event.getDate().split("-");
        holder.tvEventDay.setText(dateParts[2]); // Day number
        holder.eventDayOfWeek.setText(getDayName(event.getDate())); // Day name

        // Set event details
        holder.tvTitle.setText(event.getTitle());
        holder.tvDescription.setText(event.getDescription());
        holder.tvTime.setText(event.getStartTime() + " - " + event.getEndTime());

        // Set tags if available
        if (event.getTags() != null && !event.getTags().isEmpty()) {
            holder.tvTags.setText(String.join(", ", event.getTags()));
            holder.tvTags.setVisibility(View.VISIBLE);
        } else {
            holder.tvTags.setVisibility(View.GONE);
        }

        // Set card color
        holder.cardView.setCardBackgroundColor(event.getColor());

        // For calendar events, show remove button
        holder.btnAction.setText("Remove");
        holder.btnAction.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRemoveFromCalendar(event);
            }
        });
    }

    private void bindDiscoveryViewHolder(DiscoveryViewHolder holder, Event event) {
        // Set date in discovery format (e.g., "22\nFEB")
        String[] dateParts = event.getDate().split("-");
        try {
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Months are 0-based
            int day = Integer.parseInt(dateParts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
            String monthName = monthFormat.format(calendar.getTime()).toUpperCase();

            holder.eventDate.setText(day + "\n" + monthName);
        } catch (Exception e) {
            holder.eventDate.setText(dateParts[2] + "\n" + dateParts[1]);
        }

        // Set event details
        holder.eventTitle.setText(event.getTitle());
        holder.eventSubtitle.setText(event.getSubtitle());

        // Set tags if available
        if (event.getTags() != null && !event.getTags().isEmpty()) {
            holder.eventTags.setText(String.join(", ", event.getTags()));
            holder.eventTags.setVisibility(View.VISIBLE);
        } else {
            holder.eventTags.setVisibility(View.GONE);
        }

        // Set image
        if (event.getImageResId() != -1) {
            holder.eventImage.setImageResource(event.getImageResId());
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_image);
        }

        // For discovery events, show add to calendar button
        holder.btnAction.setText("Add to Calendar");
        holder.btnAction.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onAddToCalendar(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    private String getDayName(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date dateObj = sdf.parse(date);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
            return dayFormat.format(dateObj);
        } catch (Exception e) {
            return "";
        }
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventDay, eventDayOfWeek, tvTitle, tvDescription, tvTime, tvTags;
        CardView cardView;
        Button btnAction;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventDay = itemView.findViewById(R.id.tvEventDay);
            eventDayOfWeek = itemView.findViewById(R.id.eventDayOfWeek);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTags = itemView.findViewById(R.id.tvTags);
            cardView = itemView.findViewById(R.id.cardViewEvent);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
    public void setEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    static class DiscoveryViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventDate, eventTitle, eventSubtitle, eventTags;
        CardView cardView;
        Button btnAction;

        DiscoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventSubtitle = itemView.findViewById(R.id.eventSubtitle);
            eventTags = itemView.findViewById(R.id.eventTags);
            cardView = itemView.findViewById(R.id.eventCard);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

    }
}