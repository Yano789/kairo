package com.example.infosys_1d;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailsDialogFragment extends DialogFragment {
    private static final String ARG_EVENT = "event";
    private static final String ARG_EVENT_INDEX = "event_index";

    private Event event;
    private int eventIndex;
    private TextView nameTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView locationTextView;
    private TextView descriptionTextView;
    private Button deleteButton;
    private Button closeButton;

    public interface OnEventDeletedListener {
        void onEventDeleted(int eventIndex);
    }

    private OnEventDeletedListener listener;

    public static EventDetailsDialogFragment newInstance(Event event, int eventIndex) {
        EventDetailsDialogFragment fragment = new EventDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT, event);
        args.putInt(ARG_EVENT_INDEX, eventIndex);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEventDeletedListener(OnEventDeletedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable(ARG_EVENT);
            eventIndex = getArguments().getInt(ARG_EVENT_INDEX);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_event_details, container, false);

        nameTextView = view.findViewById(R.id.event_name);
        dateTextView = view.findViewById(R.id.event_date);
        timeTextView = view.findViewById(R.id.event_time);
        locationTextView = view.findViewById(R.id.event_location);
        descriptionTextView = view.findViewById(R.id.event_description);
        deleteButton = view.findViewById(R.id.delete_button);
        closeButton = view.findViewById(R.id.close_button);

        // Populate the dialog with event details
        nameTextView.setText(event.getText());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        dateTextView.setText(dateFormat.format(event.getDate().getTime()));

        int startHour = event.getStartHour();
        int endHour = startHour + event.getDurationHours();
        String startAmPm = startHour >= 12 ? "PM" : "AM";
        String endAmPm = endHour >= 12 ? "PM" : "AM";
        int displayStartHour = startHour % 12 == 0 ? 12 : startHour % 12;
        int displayEndHour = endHour % 12 == 0 ? 12 : endHour % 12;
        timeTextView.setText(String.format("%d:00 %s - %d:00 %s", displayStartHour, startAmPm, displayEndHour, endAmPm));

        locationTextView.setText(event.getLocation().isEmpty() ? "No location" : event.getLocation());
        descriptionTextView.setText(event.getDescription().isEmpty() ? "No description" : event.getDescription());

        // Delete button
        deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventDeleted(eventIndex);
            }
            dismiss();
        });

        // Close button
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}