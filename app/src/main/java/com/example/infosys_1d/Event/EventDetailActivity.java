package com.example.infosys_1d.Event;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys_1d.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Retrieve the Event object from Intent
        event = getIntent().getParcelableExtra("event");
        if (event == null) {
            finish(); // Close activity if no event is provided
            return;
        }

        // Initialize UI elements
        ImageView eventImage = findViewById(R.id.detailEventImage);
        TextView eventName = findViewById(R.id.detailEventName);
        TextView eventDescription = findViewById(R.id.detailEventDescription);
        TextView eventDate = findViewById(R.id.detailEventDate);
        TextView eventTime = findViewById(R.id.detailEventTime);
        TextView eventLocation = findViewById(R.id.detailEventLocation);
        FlexboxLayout tagsContainer = findViewById(R.id.tagsContainer);
        ImageButton backButton = findViewById(R.id.backButton);
        Button saveButton = findViewById(R.id.saveButton);

        // Set event data to UI elements
        eventName.setText(event.getName());
        eventDescription.setText(event.getDescription());
        eventLocation.setText(event.getLocation());

        // Set event image
        if (event.getImageResId() != 0) {
            eventImage.setImageResource(event.getImageResId());
        } else {
            eventImage.setImageResource(R.drawable.default_event_image);
        }

        // Format and set date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            eventDate.setText(sdf.format(new Date(event.getStartTime())));
        } catch (Exception e) {
            eventDate.setText(event.getDate());
        }

        // Format and set time
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String timeRange = timeFormat.format(new Date(event.getStartTime())) + " – " +
                    timeFormat.format(new Date(event.getEndTime()));
            eventTime.setText(timeRange);
        } catch (Exception e) {
            eventTime.setText("Time not available");
        }

        // Populate tags
        tagsContainer.removeAllViews();
        for (String tag : event.getTags()) {
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setChipBackgroundColorResource(R.color.light_blue);
            chip.setTextColor(getResources().getColor(android.R.color.white));
            chip.setTextSize(14);
            chip.setPadding(8, 4, 8, 4);
            tagsContainer.addView(chip);
        }

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Hide save button (not implemented in provided code)
        saveButton.setVisibility(Button.GONE);
    }
}