package com.example.infosys_1d.Event;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EventAdapter";
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
        String[] dateParts = event.getDate().split("-");
        holder.tvEventDay.setText(dateParts[2]);
        holder.eventDayOfWeek.setText(getDayName(event.getDate()));

        holder.tvTitle.setText(event.getTitle());
        holder.tvDescription.setText(event.getDescription());
        String formattedTime = formatTimeRange(event.getStartTime(), event.getEndTime());
        holder.tvTime.setText(formattedTime);

        int color;
        try {
            color = ContextCompat.getColor(context, event.getColor());
        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve color for event: " + event.getName() + ", color: " + event.getColor());
            color = ContextCompat.getColor(context, R.color.light_blue); // Fallback
        }
        Log.d(TAG, "Binding event: " + event.getName() + ", ColorRes: " + event.getColor() + ", ResolvedColor: 0x" + Integer.toHexString(color));
        holder.cardView.setCardBackgroundColor(color);

        holder.btnAction.setImageResource(R.drawable.ic_delete);
        holder.btnAction.setContentDescription("Remove event");
        holder.btnAction.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onRemoveFromCalendar(event);
            }
        });
    }

    private String formatTimeRange(long startMillis, long endMillis) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String startTime = timeFormat.format(new Date(startMillis));
        String endTime = timeFormat.format(new Date(endMillis));
        return startTime + " to " + endTime;
    }

    private void bindDiscoveryViewHolder(DiscoveryViewHolder holder, Event event) {
        String[] dateParts = event.getDate().split("-");
        try {
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int day = Integer.parseInt(dateParts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
            String monthName = monthFormat.format(calendar.getTime()).toUpperCase();

            holder.eventDate.setText(day + "\n" + monthName);
        } catch (Exception e) {
            holder.eventDate.setText(dateParts[2] + "\n" + dateParts[1]);
        }

        holder.eventTitle.setText(event.getTitle());
        holder.eventSubtitle.setText(event.getSubtitle());

        if (event.getImageResId() != -1) {
            holder.eventImage.setImageResource(event.getImageResId());
        } else {
            holder.eventImage.setImageResource(R.drawable.default_event_image);
        }

        holder.btnAction.setImageResource(R.drawable.ic_add);
        holder.btnAction.setContentDescription("Add to calendar");
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

    public void setEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventDay, eventDayOfWeek, tvTitle, tvDescription, tvTime;
        CardView cardView;
        ImageButton btnAction;

        CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventDay = itemView.findViewById(R.id.tvEventDay);
            eventDayOfWeek = itemView.findViewById(R.id.eventDayOfWeek);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            cardView = itemView.findViewById(R.id.cardViewEvent);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }

    static class DiscoveryViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventDate, eventTitle, eventSubtitle;
        CardView cardView;
        ImageButton btnAction;

        DiscoveryViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventSubtitle = itemView.findViewById(R.id.eventSubtitle);
            cardView = itemView.findViewById(R.id.eventCard);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}