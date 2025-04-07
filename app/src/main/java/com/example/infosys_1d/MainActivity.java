package com.example.infosys_1d;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();
        Fragment calendarFragment = new CalendarFragment();
        Fragment notificationsFragment = new NotificationsFragment();
        Fragment scheduleFragment = new ScheduleFragment();
        Fragment profileFragmentStudent = new ProfileFragmentStudent();
        Fragment profileFragmentStaff = new ProfileFragmentStaff();
        Fragment profileFragmentOrg = new ProfileFragmentOrg();

        setCurrentFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home) {
                setCurrentFragment(homeFragment);
            } else if (id == R.id.events_calendar) {
                setCurrentFragment(calendarFragment);
            } else if (id == R.id.notifications) {
                setCurrentFragment(notificationsFragment);
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