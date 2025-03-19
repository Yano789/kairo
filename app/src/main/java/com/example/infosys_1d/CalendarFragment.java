package com.example.infosys_1d;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private TextView monthLabel;
    private TextView[] dayLabels;
    private Calendar currentWeek;
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
    private List<Event> events;
    private View fragmentView; // Store the view for refreshing the display

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        fragmentView = view; // Store the view

        // Initialize views
        monthLabel = view.findViewById(R.id.month_label);
        ImageButton prevWeekButton = view.findViewById(R.id.prev_week);
        ImageButton nextWeekButton = view.findViewById(R.id.next_week);
        ImageButton settingsButton = view.findViewById(R.id.settings_button);
        ImageButton todayButton = view.findViewById(R.id.today_button);
        ImageButton addEventButton = view.findViewById(R.id.add_event_button);

        dayLabels = new TextView[]{
                view.findViewById(R.id.day1),
                view.findViewById(R.id.day2),
                view.findViewById(R.id.day3),
                view.findViewById(R.id.day4),
                view.findViewById(R.id.day5),
                view.findViewById(R.id.day6),
                view.findViewById(R.id.day7)
        };

        // Initialize the event list with static events
        events = new ArrayList<>();
        initializeEvents();

        // Initialize the current week to the current week (starting on Monday)
        currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start the week on Monday

        // Update the display
        updateWeekDisplay(view);

        // Set button listeners
        prevWeekButton.setOnClickListener(v -> changeWeek(-1, view));
        nextWeekButton.setOnClickListener(v -> changeWeek(1, view));
        settingsButton.setOnClickListener(v -> {
            // Handle settings button click (e.g., open a settings screen)
            android.util.Log.d("CalendarFragment", "Settings button clicked");
        });
        todayButton.setOnClickListener(v -> goToToday(view));
        addEventButton.setOnClickListener(v -> showAddEventDialog());

        return view;
    }

    private void initializeEvents() {
        // Add static events as shown in the image
        // Monday, February 18th: 50.001 from 11 AM to 1 PM (orange)
        Calendar date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 18);
        events.add(new Event(date, 11, 13, "50.001", "", "", 0xFFF5C77E));

        // Tuesday, February 19th: 50.004 from 10 AM to 2 PM (blue)
        date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 19);
        events.add(new Event(date, 10, 14, "50.004\nLecture", "", "", 0xFFA3CFFA));

        // Thursday, February 21st: 50.002 from 12 PM to 2 PM (pink)
        date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 21);
        events.add(new Event(date, 12, 14, "50.002", "", "", 0xFFF5A3A3));

        // Thursday, February 21st: 50.002 from 4 PM to 5 PM (pink)
        date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 21);
        events.add(new Event(date, 16, 17, "50.002\nLecture", "", "", 0xFFF5A3A3));

        // Saturday, February 23rd: 50.001 from 9 AM to 5 PM (orange)
        date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 23);
        events.add(new Event(date, 9, 17, "50.001\nLecture", "", "", 0xFFF5C77E));

        // Sunday, February 24th: 50.001 from 2 PM to 4 PM (orange)
        date = Calendar.getInstance();
        date.set(2025, Calendar.FEBRUARY, 24);
        events.add(new Event(date, 14, 16, "50.001", "", "", 0xFFF5C77E));

        // Add a sample event for March 19th, 2025 (today's date) to test
        date = Calendar.getInstance();
        date.set(2025, Calendar.MARCH, 19);
        events.add(new Event(date, 10, 12, "Sample Event", "Room 101", "Team meeting", 0xFF90EE90)); // Light green
    }

    private void showAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment();
        dialog.setOnEventAddedListener(event -> {
            Log.d("CalendarFragment", "Received new event: " + event.getText() +
                    ", Date=" + event.getDate().getTime() +
                    ", StartHour=" + event.getStartHour() +
                    ", Duration=" + event.getDurationHours());
            events.add(event);
            Log.d("CalendarFragment", "Events list size: " + events.size());
            updateWeekDisplay(fragmentView); // Refresh the display
        });
        dialog.show(getParentFragmentManager(), "AddEventDialog");
    }

    private void showEventDetailsDialog(Event event, int eventIndex) {
        EventDetailsDialogFragment dialog = EventDetailsDialogFragment.newInstance(event, eventIndex);
        dialog.setOnEventDeletedListener(index -> {
            events.remove(index);
            Log.d("CalendarFragment", "Event deleted at index: " + index + ", New list size: " + events.size());
            updateWeekDisplay(fragmentView); // Refresh the display
        });
        dialog.show(getParentFragmentManager(), "EventDetailsDialog");
    }

    private void changeWeek(int amount, View view) {
        currentWeek.add(Calendar.WEEK_OF_YEAR, amount);
        updateWeekDisplay(view);
    }

    private void goToToday(View view) {
        // Set the current week to the week containing today's date
        currentWeek = Calendar.getInstance();
        currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Start the week on Monday
        updateWeekDisplay(view);
    }

    private void updateWeekDisplay(View view) {
        // Update the month label
        monthLabel.setText(monthFormat.format(currentWeek.getTime()));

        // Update the day labels (date and day of week) and find the current date's column
        Calendar tempCalendar = (Calendar) currentWeek.clone();
        int currentDateColumn = -1; // -1 means no highlight (current date not in this week)
        for (int i = 0; i < dayLabels.length; i++) {
            String date = dateFormat.format(tempCalendar.getTime());
            String day = dayFormat.format(tempCalendar.getTime());
            dayLabels[i].setText(date + "\n" + day);

            // Highlight the current day with a circular bubble
            Calendar today = Calendar.getInstance();
            if (tempCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                    tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                dayLabels[i].setBackgroundResource(R.drawable.circle_background);
                currentDateColumn = i; // Store the column index of the current date
            } else {
                dayLabels[i].setBackgroundResource(0); // Clear background
            }

            // Move to the next day
            tempCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Update event blocks and highlight the current date's column
        updateEventBlocks(view, currentDateColumn);
    }

    private void updateEventBlocks(View view, int currentDateColumn) {
        // Clear existing event blocks and reset backgrounds
        String[] days = new String[]{"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
        for (int hour = 8; hour <= 18; hour++) {
            for (int dayIndex = 0; dayIndex < days.length; dayIndex++) {
                String day = days[dayIndex];
                int slotId = getResources().getIdentifier("slot_" + (hour % 12 == 0 ? 12 : hour % 12) + (hour < 12 ? "am" : "pm") + "_" + day, "id", requireContext().getPackageName());
                FrameLayout slot = view.findViewById(slotId);
                if (slot != null) {
                    slot.removeAllViews();
                    // Highlight the slot if it’s in the current date's column
                    if (dayIndex == currentDateColumn) {
                        slot.setBackgroundColor(0xFFD3E8F5); // Light blue highlight
                    } else {
                        slot.setBackgroundColor(0xFFFFFFFF); // White background
                    }
                }
            }
        }

        // Get the start and end dates of the displayed week
        Calendar weekStart = (Calendar) currentWeek.clone();
        Calendar weekEnd = (Calendar) currentWeek.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6); // End of the week (Sunday)

        // Filter events for the displayed week and add them
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            Calendar eventDate = event.getDate();
            // Check if the event date is within the displayed week
            if (!eventDate.before(weekStart) && !eventDate.after(weekEnd)) {
                // Calculate the day index (0 for Monday, 6 for Sunday)
                int dayIndex = (eventDate.get(Calendar.DAY_OF_YEAR) - weekStart.get(Calendar.DAY_OF_YEAR)) % 7;
                if (dayIndex < 0) dayIndex += 7; // Handle edge cases with year boundaries
                String day = days[dayIndex];
                addEvent(view, day, event.getStartHour(), event.getDurationHours(), event.getText(), event.getColor(), event, i);
            }
        }
    }

    private void addEvent(View view, String day, int startHour, int durationHours, String eventText, int color, Event event, int eventIndex) {
        // Find the starting slot
        String period = startHour < 12 ? "am" : "pm";
        int displayHour = startHour % 12 == 0 ? 12 : startHour % 12;
        int slotId = getResources().getIdentifier("slot_" + displayHour + period + "_" + day, "id", requireContext().getPackageName());
        FrameLayout slot = view.findViewById(slotId);

        if (slot != null) {
            // Create a TextView for the event
            TextView eventView = new TextView(requireContext());
            eventView.setText(eventText);
            eventView.setBackgroundColor(color);
            eventView.setTextColor(0xFF000000);
            eventView.setTextSize(12);
            eventView.setGravity(android.view.Gravity.CENTER);
            eventView.setPadding(4, 4, 4, 4);

            // Make the event clickable
            eventView.setOnClickListener(v -> showEventDetailsDialog(event, eventIndex));

            // Calculate the height based on duration (60dp per hour)
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    60 * durationHours
            );
            eventView.setLayoutParams(params);

            // Add the event to the slot
            slot.addView(eventView);
        } else {
            Log.e("CalendarFragment", "Slot not found for: slot_" + displayHour + period + "_" + day);
        }
    }
}