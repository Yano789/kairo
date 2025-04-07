package com.example.infosys_1d;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get the Event object from the Intent
        Event event = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            event = getIntent().getParcelableExtra("event", Event.class);
        }

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
            eventStartTime.setText(event.getStartTime());
            eventEndTime.setText(event.getEndTime());
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