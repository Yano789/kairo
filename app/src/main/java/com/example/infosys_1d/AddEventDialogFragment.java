package com.example.infosys_1d;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class AddEventDialogFragment extends DialogFragment {
    private EditText nameEditText;
    private EditText dateEditText;
    private EditText startTimeEditText;
    private EditText endTimeEditText;
    private EditText locationEditText;
    private EditText descriptionEditText;
    private Button saveButton;
    private Button cancelButton;

    private Calendar selectedDate;
    private int startHour; // In 24-hour format
    private int endHour;   // In 24-hour format

    public interface OnEventAddedListener {
        void onEventAdded(Event event);
    }

    private OnEventAddedListener listener;

    public void setOnEventAddedListener(OnEventAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_event, container, false);

        nameEditText = view.findViewById(R.id.event_name);
        dateEditText = view.findViewById(R.id.event_date);
        startTimeEditText = view.findViewById(R.id.event_start_time);
        endTimeEditText = view.findViewById(R.id.event_end_time);
        locationEditText = view.findViewById(R.id.event_location);
        descriptionEditText = view.findViewById(R.id.event_description);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        // Initialize the date and time pickers
        selectedDate = Calendar.getInstance();
        startHour = 8; // Default to 8 AM
        endHour = 9;   // Default to 9 AM

        // Date picker
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        dateEditText.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Start time picker (12-hour format with AM/PM)
        startTimeEditText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view12, hourOfDay, minute) -> {
                        // Convert 12-hour format with AM/PM to 24-hour format
                        startHour = hourOfDay;
                        String amPm = hourOfDay >= 12 ? "PM" : "AM";
                        int displayHour = hourOfDay % 12;
                        if (displayHour == 0) displayHour = 12;
                        startTimeEditText.setText(String.format("%d:%02d %s", displayHour, minute, amPm));
                    },
                    startHour, 0, false // Use 12-hour format with AM/PM
            );
            timePickerDialog.show();
        });

        // End time picker (12-hour format with AM/PM)
        endTimeEditText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view13, hourOfDay, minute) -> {
                        // Convert 12-hour format with AM/PM to 24-hour format
                        endHour = hourOfDay;
                        String amPm = hourOfDay >= 12 ? "PM" : "AM";
                        int displayHour = hourOfDay % 12;
                        if (displayHour == 0) displayHour = 12;
                        endTimeEditText.setText(String.format("%d:%02d %s", displayHour, minute, amPm));
                    },
                    endHour, 0, false // Use 12-hour format with AM/PM
            );
            timePickerDialog.show();
        });

        // Save button
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            // Basic validation: ensure name is not empty and end time is after start time
            if (name.isEmpty()) {
                nameEditText.setError("Event name is required");
                return;
            }

            if (endHour <= startHour) {
                endTimeEditText.setError("End time must be after start time");
                return;
            }

            // Log the event details for debugging
            Log.d("AddEventDialog", "Saving event: " +
                    "Name=" + name +
                    ", Date=" + selectedDate.getTime() +
                    ", StartHour=" + startHour +
                    ", EndHour=" + endHour +
                    ", Location=" + location +
                    ", Description=" + description);

            // Create the event
            Event event = new Event(selectedDate, startHour, endHour, name, location, description, 0xFF90EE90); // Light green color
            if (listener != null) {
                listener.onEventAdded(event);
                Log.d("AddEventDialog", "Event added to listener");
            } else {
                Log.e("AddEventDialog", "Listener is null");
            }
            dismiss();
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> dismiss());

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