package com.example.infosys_1d.Event;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private Event event;
    private EditText editEventName, editEventDescription, editEventLocation;
    private TextView eventDate, eventStartTime, eventTags;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get the Event object from the Intent
        event = getIntent().getParcelableExtra("event");

        // Find views
        ImageView eventImage = findViewById(R.id.detailEventImage);
        TextView eventName = findViewById(R.id.detailEventName);
        editEventName = findViewById(R.id.detailEventName); // Will be converted to EditText in XML
        eventDate = findViewById(R.id.detailEventDate);
        TextView eventDescription = findViewById(R.id.detailEventDescription);
        editEventDescription = findViewById(R.id.detailEventDescription);
        TextView eventStartTime = findViewById(R.id.detailEventStartTime);
        TextView eventLocation = findViewById(R.id.detailEventLocation);
        editEventLocation = findViewById(R.id.detailEventLocation);
        eventTags = findViewById(R.id.detailEventTags);
        saveButton = findViewById(R.id.saveButton);

        // Check if this is a personal event
        boolean isPersonal = event != null && event.getTags().contains("personal");

        // Set editability based on tag
        if (isPersonal) {
            // Make views editable
            editEventName.setFocusableInTouchMode(true);
            editEventDescription.setFocusableInTouchMode(true);
            editEventLocation.setFocusableInTouchMode(true);

            // Show save button
            saveButton.setVisibility(View.VISIBLE);

            // Set click listener for save button
            saveButton.setOnClickListener(v -> saveChanges());
        } else {
            // Make views non-editable
            editEventName.setFocusable(false);
            editEventDescription.setFocusable(false);
            editEventLocation.setFocusable(false);

            // Hide save button
            saveButton.setVisibility(View.GONE);
        }

        // Set the event details
        if (event != null) {
            editEventName.setText(event.getTitle());
            eventDescription.setText(event.getDescription());

            // Format date
            Date startDate = new Date(event.getStartTime());
            Date endDate = new Date(event.getEndTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma", Locale.getDefault());

            String formattedDate = dateFormat.format(startDate);
            String formattedStartTime = timeFormat.format(startDate).toLowerCase();
            String formattedEndTime = timeFormat.format(endDate).toLowerCase();

            eventDate.setText(formattedDate);
            eventStartTime.setText(formattedStartTime + " - " + formattedEndTime);
            eventLocation.setText(event.getLocation());

            // Set the tags
            String tags = "Tags: " + String.join(", ", event.getTags());
            eventTags.setText(tags);

            // Set the image
            if (event.getImageResId() == -1) {
                eventImage.setImageResource(R.drawable.default_event_image);
            } else {
                eventImage.setImageResource(event.getImageResId());
            }
        }
    }

    private void saveChanges() {
        if (event != null) {
            // Update the event object with edited values
            event.setTitle(editEventName.getText().toString());
            event.setDescription(editEventDescription.getText().toString());
            event.setLocation(editEventLocation.getText().toString());

            // Here you would typically save the changes to your database
            // For now just show a toast
            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();

            // Optional: Close the activity after saving
            // finish();
        }
    }
}