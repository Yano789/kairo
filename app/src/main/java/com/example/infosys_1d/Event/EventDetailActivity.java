package com.example.infosys_1d.Event;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;
import com.google.android.flexbox.FlexboxLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    private Event event;
    private TextView eventNameView, eventDescriptionView, eventLocationView;
    private TextView eventDate, eventStartTime;
    private Button saveButton;
    private ImageButton backButton;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get user email
        userEmail = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("user_email", "");
        Log.d(TAG, "User email: " + userEmail);

        // Get the Event object from the Intent
        event = getIntent().getParcelableExtra("event");

        // Find views
        ImageView eventImage = findViewById(R.id.detailEventImage);
        eventNameView = findViewById(R.id.detailEventName);
        eventDescriptionView = findViewById(R.id.detailEventDescription);
        eventLocationView = findViewById(R.id.detailEventLocation);
        eventDate = findViewById(R.id.detailEventDate);
        eventStartTime = findViewById(R.id.detailEventTime);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        if (event == null) {
            Log.e(TAG, "No event provided");
            Toast.makeText(this, "Error: No event data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Log color for debugging
        try {
            int color = ContextCompat.getColor(this, event.getColor());
            Log.d(TAG, "Event: " + event.getTitle() + ", ColorRes: " + event.getColor() + ", ResolvedColor: 0x" + Integer.toHexString(color));
        } catch (Exception e) {
            Log.e(TAG, "Failed to resolve color: " + event.getColor());
        }

        // Check if this is a personal event
        boolean isPersonal = event.getTags().contains("personal");

        // Render tags
        if (event.getTags() != null && !event.getTags().isEmpty()) {
            renderTagChips(event.getTags());
        }

        // Set back button listener
        backButton.setOnClickListener(v -> finish());

        // Set editability based on tag
        if (isPersonal) {
            // Make views editable
            eventNameView.setFocusableInTouchMode(true);
            eventNameView.setFocusable(true);
            eventDescriptionView.setFocusableInTouchMode(true);
            eventDescriptionView.setFocusable(true);
            eventLocationView.setFocusableInTouchMode(true);
            eventLocationView.setFocusable(true);

            // Show save button
            saveButton.setVisibility(View.VISIBLE);

            // Set click listener for save button
            saveButton.setOnClickListener(v -> saveChanges());
        } else {
            // Make views non-editable
            eventNameView.setFocusable(false);
            eventDescriptionView.setFocusable(false);
            eventLocationView.setFocusable(false);

            // Hide save button
            saveButton.setVisibility(View.GONE);
        }

        // Set the event details
        eventNameView.setText(event.getTitle());
        eventDescriptionView.setText(event.getDescription());

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
        eventLocationView.setText(event.getLocation());

        // Set the image
        if (event.getImageResId() == -1) {
            eventImage.setImageResource(R.drawable.default_event_image);
        } else {
            eventImage.setImageResource(event.getImageResId());
        }
    }

    private void saveChanges() {
        if (event == null || userEmail.isEmpty()) {
            Log.e(TAG, "Cannot save: event is null or no user logged in");
            Toast.makeText(this, "Error: Cannot save changes", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Update the event object with edited values
            String newTitle = eventNameView.getText().toString().trim();
            String newDescription = eventDescriptionView.getText().toString().trim();
            String newLocation = eventLocationView.getText().toString().trim();

            // Validate inputs
            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            event.setTitle(newTitle);
            event.setDescription(newDescription.isEmpty() ? event.getTitle() + " scheduled by Kai" : newDescription);
            event.setLocation(newLocation.isEmpty() ? "Not specified" : newLocation);

            // Save to UserRepository
            UserRepository.removeUserEvent(userEmail, event); // Remove old event
            UserRepository.addPersonalEventToUser(userEmail, event); // Add updated event
            Log.d(TAG, "Event updated: " + event.getTitle() + ", ID: " + event.getId());

            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to save event: " + e.getMessage());
            Toast.makeText(this, "Error saving changes", Toast.LENGTH_SHORT).show();
        }
    }

    private void renderTagChips(List<String> tags) {
        FlexboxLayout tagsContainer = findViewById(R.id.tagsContainer);
        tagsContainer.removeAllViews();

        for (String tag : tags) {
            TextView chip = new TextView(this);
            chip.setText(tag);
            chip.setTextSize(13);
            chip.setTextColor(Color.parseColor("#444444"));
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_bg));
            chip.setPadding(32, 16, 32, 16);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 16);
            chip.setLayoutParams(params);

            tagsContainer.addView(chip);
        }
    }
}