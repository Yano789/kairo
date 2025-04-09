package com.example.infosys_1d.Event;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get the Event object from the Intent
        Event event = null;
        event = (Event) getIntent().getParcelableExtra("event");

        // Find views
        ImageView eventImage = findViewById(R.id.detailEventImage);
        TextView eventName = findViewById(R.id.detailEventName);
        TextView eventDate = findViewById(R.id.detailEventDate);
        TextView eventDescription = findViewById(R.id.detailEventDescription);
        TextView eventStartTime = findViewById(R.id.detailEventStartTime);
        TextView eventEndTime = findViewById(R.id.detailEventEndTime);
        TextView eventLocation = findViewById(R.id.detailEventLocation);
        TextView eventTags = findViewById(R.id.detailEventTags);  // New TextView for tags

        // Set the event details
        if (event != null) {
            eventName.setText(event.getTitle());
            eventDate.setText(event.getDate());
            eventDescription.setText(event.getDescription());
            // Format date
            Date startDate = new Date(event.getStartTime());
            Date endDate = new Date(event.getEndTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.getDefault());

            String formattedDate = dateFormat.format(startDate);
            String formattedStartTime = timeFormat.format(startDate).toLowerCase(); // e.g., 9:00am
            String formattedEndTime = timeFormat.format(endDate).toLowerCase();     // e.g., 12:00pm

            eventDate.setText(formattedDate);
            eventStartTime.setText(formattedStartTime + " - " + formattedEndTime);
            eventEndTime.setVisibility(View.GONE);
            eventLocation.setText(event.getLocation());

            // Set the tags
            String tags = "Tags: " + String.join(", ", event.getTags());
            eventTags.setText(tags);

            // Set the image, using the default if none is provided
            if (event.getImageResId() == -1) {
                eventImage.setImageResource(R.drawable.default_event_image);
            } else {
                eventImage.setImageResource(event.getImageResId());
            }
        }
    }
}