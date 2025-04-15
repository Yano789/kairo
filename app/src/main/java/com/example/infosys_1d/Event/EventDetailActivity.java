package com.example.infosys_1d.Event;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;

public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = "EventDetailActivity";
    private String userEmail;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Hypothetical initialization
        userEmail = getIntent().getStringExtra("user_email");
        event = (Event) getIntent().getSerializableExtra("event");

        // Other setup (e.g., UI bindings)
    }

    // Hypothetical method where lines 160-161 exist (e.g., updating an event)
    private void updateEvent(Event updatedEvent) {
        try {
            // Line ~160: Remove old event
            UserRepository.removeUserEvent(userEmail, event, this);
            // Line ~161: Add updated event
            UserRepository.addPersonalEventToUser(userEmail, updatedEvent, this);
            Log.d(TAG, "Updated event for " + userEmail);
        } catch (Exception e) {
            Log.e(TAG, "Failed to update event for " + userEmail + ": " + e.getMessage());
        }
    }
}