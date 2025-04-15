package com.example.infosys_1d;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.infosys_1d.Calendar.CalendarFragment;
import com.example.infosys_1d.Chatbot.ChatFragment;
import com.example.infosys_1d.Discovery.HomeFragment;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.ProfilePage.ProfileFragmentOrg;
import com.example.infosys_1d.ProfilePage.ProfileFragmentStudent;
import com.example.infosys_1d.Schedule.ScheduleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AppContext and EventRepository
        AppContext.setAppContext(this);
        Log.d(TAG, "Initialized AppContext and EventRepository");

        // Initialize events
        EventRepository.loadDummyEvents(this);
        UserRepository.initializeEvents(this);

        // Get email from Intent
        String userEmail = getIntent().getStringExtra("user_email");
        if (userEmail != null && !userEmail.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("user_email", userEmail);
            editor.apply();
            Log.d(TAG, "Received and saved user_email: " + userEmail);
        } else {
            Log.w(TAG, "No user_email received in Intent");
        }

        // Log event counts for debugging
        Log.d(TAG, "General events count: " + EventRepository.getGeneralEvents().size());
        if (userEmail != null) {
            Log.d(TAG, "Personal events count for " + userEmail + ": " + EventRepository.getCalendarEvents(userEmail).size());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment calendarFragment = new CalendarFragment();
        Fragment chatFragment = new ChatFragment();
        Fragment scheduleFragment = new ScheduleFragment();
        Fragment profileFragmentStudent = new ProfileFragmentStudent();
        Fragment profileFragmentOrg = new ProfileFragmentOrg();

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                setCurrentFragment(homeFragment);
            } else if (id == R.id.events_calendar) {
                setCurrentFragment(calendarFragment);
            } else if (id == R.id.notifications) {
                setCurrentFragment(chatFragment);
            } else if (id == R.id.schedule) {
                setCurrentFragment(scheduleFragment);
            } else if (id == R.id.profile) {
                setCurrentFragment(profileFragmentStudent);
            }

            return true;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}