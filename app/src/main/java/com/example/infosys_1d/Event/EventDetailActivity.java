package com.example.infosys_1d.Event;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import com.google.android.flexbox.FlexboxLayout;


public class EventDetailActivity extends AppCompatActivity {

    private Event event;
    private TextView eventNameView, eventDescriptionView, eventLocationView;
    private TextView eventDate, eventStartTime;
    private Button saveButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

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
        ImageButton backButton = findViewById(R.id.backButton);

        // Check if this is a personal event
        boolean isPersonal = event != null && event.getTags().contains("personal");

        if (event != null && event.getTags() != null && !event.getTags().isEmpty()){
            renderTagChips(event.getTags());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // this will close the current activity
            }
        });

        // Set editability based on tag
        if (isPersonal) {
            // Make views editable
            eventNameView.setFocusableInTouchMode(true);
            eventDescriptionView.setFocusableInTouchMode(true);
            eventLocationView.setFocusableInTouchMode(true);

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
        if (event != null) {
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
    }

    private void saveChanges() {
        if (event != null) {
            // Update the event object with edited values
            event.setTitle(eventNameView.getText().toString());
            event.setDescription(eventDescriptionView.getText().toString());
            event.setLocation(eventLocationView.getText().toString());

            // Here you would typically save the changes to your database
            // For now just show a toast
            Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();

            // Optional: Close the activity after saving
            // finish();
        }
    }

    private void renderTagChips(List<String> tags){
        FlexboxLayout tagsContainer = findViewById(R.id.tagsContainer);
        tagsContainer.removeAllViews();

        for (String tag : tags){
            TextView chip = new TextView(this);
            chip.setText(tag);
            chip.setTextSize(13);
            chip.setTextColor(Color.parseColor("#444444"));
            chip.setBackground(ContextCompat.getDrawable(this,R.drawable.chip_bg));
            chip.setPadding(32,16,32,16);

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,16,16);
            chip.setLayoutParams(params);

            tagsContainer.addView(chip);

        }
    }
}