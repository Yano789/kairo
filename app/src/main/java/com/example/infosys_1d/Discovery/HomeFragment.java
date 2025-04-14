package com.example.infosys_1d.Discovery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.Event.EventAdapter;
import com.example.infosys_1d.Event.EventRepository;
import com.example.infosys_1d.Event.EventViewModel;
import com.example.infosys_1d.Login.Admin;
import com.example.infosys_1d.Login.UserRepository;
import com.example.infosys_1d.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerView;
    private Button toggleEventTypeButton;
    private ImageButton filterButton;
    private FloatingActionButton fabAddEvent;

    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private final List<Event> displayedEvents = new ArrayList<>();
    private final Set<String> selectedTags = new HashSet<>();
    private boolean showFifthrowEvents = false;

    private View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        EventRepository.loadDummyEvents(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        emptyView = view.findViewById(R.id.emptyView);
        toggleEventTypeButton = view.findViewById(R.id.toggleEventTypeButton);
        filterButton = view.findViewById(R.id.filterButton);
        fabAddEvent = view.findViewById(R.id.fab_add_event);

        // Set initial visibility
        emptyView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up button listeners
        if (toggleEventTypeButton != null) {
            toggleEventTypeButton.setOnClickListener(v -> {
                showFifthrowEvents = !showFifthrowEvents;
                toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
                eventViewModel.refreshDiscoverableEvents();
            });
        }

        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showTagFilterDialog());
        }

        // Set up FAB visibility and listener
        String userEmail = getCurrentUserEmail();
        boolean isAdmin = isAdminUser(userEmail);
        Log.d(TAG, "User: " + userEmail + ", isAdmin: " + isAdmin);
        fabAddEvent.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        if (isAdmin) {
            fabAddEvent.setOnClickListener(v -> showAddEventDialog());
        }

        // Set up the RecyclerView
        setupRecyclerView();

        // Set up the observers
        setupObservers();
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(requireContext(), displayedEvents,
                R.layout.discovery_item_event, new EventAdapter.OnEventActionListener() {
            @Override
            public void onAddToCalendar(Event event) {
                Log.d(TAG, "Adding event to calendar: " + event.getName() + " with ID " + event.getId() + " for " + getCurrentUserEmail());
                EventRepository.moveToCalendar(getCurrentUserEmail(), event);
                eventViewModel.refreshDiscoverableEvents();
            }

            @Override
            public void onRemoveFromCalendar(Event event) {
                Log.d(TAG, "Removing event from calendar: " + event.getName() + " with ID " + event.getId() + " for " + getCurrentUserEmail());
                EventRepository.removeFromCalendar(getCurrentUserEmail(), event);
                eventViewModel.refreshDiscoverableEvents();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(eventAdapter);
    }

    private void setupObservers() {
        eventViewModel.getAllEvents().observe(getViewLifecycleOwner(), events -> {
            Log.d(TAG, "Received " + events.size() + " events from ViewModel");
            filterAndDisplayEvents(events);
        });
        eventViewModel.refreshDiscoverableEvents();
    }

    private void filterAndDisplayEvents(List<Event> allEvents) {
        displayedEvents.clear();
        String userEmail = getCurrentUserEmail();
        List<Event> userEvents = UserRepository.getUserEvents(userEmail);
        Log.d(TAG, "User events for " + userEmail + ": " + userEvents.size());

        for (Event event : allEvents) {
            boolean isFifthrow = event.getTags().contains("fifthrow");
            boolean matchesType = (showFifthrowEvents == isFifthrow);
            boolean matchesTags = selectedTags.isEmpty() || containsAny(event.getTags(), selectedTags);
            boolean isPersonal = userEvents.stream().anyMatch(e -> e.getId().equals(event.getId()));

            Log.d(TAG, "Event: " + event.getName() + ", ID: " + event.getId() +
                    ", isFifthrow=" + isFifthrow + ", matchesType=" + matchesType +
                    ", matchesTags=" + matchesTags + ", isPersonal=" + isPersonal);

            if (matchesType && matchesTags && !isPersonal) {
                displayedEvents.add(event);
            }
        }

        // Sort by start time (earliest first)
        displayedEvents.sort(Comparator.comparingLong(Event::getStartTime));

        Log.d(TAG, "Displayed events: " + displayedEvents.size());
        eventAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private boolean containsAny(List<String> eventTags, Set<String> selectedTags) {
        for (String tag : eventTags) {
            if (selectedTags.contains(tag)) return true;
        }
        return false;
    }

    private void updateEmptyView() {
        View rootView = getView();
        if (rootView != null) {
            boolean showEmptyView = displayedEvents.isEmpty();
            rootView.findViewById(R.id.emptyView).setVisibility(showEmptyView ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(showEmptyView ? View.GONE : View.VISIBLE);
        }
    }

    private String getCurrentUserEmail() {
        return requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getString("user_email", "");
    }

    private boolean isAdminUser(String userEmail) {
        for (Admin admin : UserRepository.getSampleAdmins()) {
            if (admin.getEmail().equalsIgnoreCase(userEmail)) {
                return true;
            }
        }
        return false;
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_event, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.event_name);
        EditText dateEditText = dialogView.findViewById(R.id.event_date);
        EditText startTimeEditText = dialogView.findViewById(R.id.event_start_time);
        EditText locationEditText = dialogView.findViewById(R.id.event_location);
        EditText descriptionEditText = dialogView.findViewById(R.id.event_description);
        CheckBox fifthrowCheckBox = dialogView.findViewById(R.id.fifthrow_check);
        Button saveButton = dialogView.findViewById(R.id.save_event_button);

        // Date picker
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        dateEditText.setText(date);
                    }, year, month, day);
            datePicker.show();
        });

        // Time picker
        startTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        startTimeEditText.setText(time);
                    }, hour, minute, true);
            timePicker.show();
        });

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String startTime = startTimeEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            boolean isFifthrow = fifthrowCheckBox.isChecked();

            if (name.isEmpty() || date.isEmpty() || startTime.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in name, date, and start time", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long startTimeMillis = EventRepository.convertTimeToMillis(date, startTime);
                if (startTimeMillis == 0) {
                    Toast.makeText(requireContext(), "Invalid date or time format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Assume 1-hour duration
                long endTimeMillis = startTimeMillis + 60 * 60 * 1000;

                List<String> tags = new ArrayList<>();
                if (isFifthrow) {
                    tags.add("fifthrow");
                }
                tags.add(name.toLowerCase().replace(" ", ""));

                Event event = new Event(
                        name,
                        description.isEmpty() ? "No description" : description,
                        location.isEmpty() ? "TBD" : location,
                        startTimeMillis,
                        endTimeMillis,
                        date,
                        tags,
                        R.color.light_blue, // Default color
                        name,
                        name,
                        R.drawable.default_event_image
                );

                EventRepository.addGeneralEvent(event);
                eventViewModel.refreshDiscoverableEvents();
                Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Failed to add event: " + e.getMessage());
                Toast.makeText(requireContext(), "Error adding event", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showTagFilterDialog() {
        List<String> allTags = eventViewModel.getAllTags();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_tag_filter, null);
        builder.setView(dialogView);

        ListView tagListView = dialogView.findViewById(R.id.tagListView);
        EditText searchBar = dialogView.findViewById(R.id.searchBar);
        Button applyButton = dialogView.findViewById(R.id.applyButton);

        TagFilterAdapter tagAdapter = new TagFilterAdapter(requireActivity(), allTags);
        tagListView.setAdapter(tagAdapter);

        boolean[] checkedStates = new boolean[allTags.size()];
        for (int i = 0; i < allTags.size(); i++) {
            checkedStates[i] = selectedTags.contains(allTags.get(i));
        }
        tagAdapter.setCheckedStates(checkedStates);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tagAdapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        AlertDialog dialog = builder.create();
        applyButton.setOnClickListener(v -> {
            selectedTags.clear();
            boolean[] newCheckedStates = tagAdapter.getCheckedStates();
            for (int i = 0; i < newCheckedStates.length; i++) {
                if (newCheckedStates[i]) {
                    selectedTags.add(allTags.get(i));
                }
            }
            eventViewModel.refreshDiscoverableEvents();
            dialog.dismiss();
        });

        dialog.show();
    }
}