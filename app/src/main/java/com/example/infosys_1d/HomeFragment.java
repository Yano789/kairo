package com.example.infosys_1d;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;  // Add this import
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private List<Event> allEvents; // Store all events for filtering
    private Button toggleEventTypeButton;
    private ImageButton filterButton;
    private boolean showFifthrowEvents = false; // false = General, true = Fifthrow
    private Set<String> selectedTags = new HashSet<>(); // Store selected tags
    private List<String> allTags; // Store all possible tags

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Toggle Button
        toggleEventTypeButton = view.findViewById(R.id.toggleEventTypeButton);
        toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
        toggleEventTypeButton.setOnClickListener(v -> {
            showFifthrowEvents = !showFifthrowEvents;
            toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
            filterEvents();
        });

        // Initialize Filter Button (now an ImageButton)
        filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showTagFilterDialog());

        // Sample data for events with tags
        allEvents = new ArrayList<>();
        allEvents.add(new Event(
                "The World's First Design AI",
                "22\nFEB",
                "Join us for an open house showcasing the world's first AI designed for creative professionals.",
                "10:00 AM",
                "4:00 PM",
                "Innovation Hub, 123 Tech Street",
                R.drawable.default_event_image,
                Arrays.asList("tech", "ai", "design"),
                "Open House 2025"
        ));
        allEvents.add(new Event(
                "Picnic at the Disco II",
                "27\nFEB",
                "A fun-filled day of music, dancing, and outdoor activities for all ages.",
                "12:00 PM",
                "6:00 PM",
                "Central Park, 456 Dance Avenue",
                Arrays.asList("music", "dance", "fifthrow"),
                "Picnic at the Disco II"
        ));
        allEvents.add(new Event(
                "CCA Art Exhibition",
                "28\nFEB",
                "An exhibition showcasing student artwork from the CCA program.",
                "1:00 PM",
                "5:00 PM",
                "Art Gallery, 789 Creative Lane",
                R.drawable.default_event_image,
                Arrays.asList("art", "cca", "exhibition"),
                "CCA Art Exhibition"
        ));

        // Collect all unique tags
        allTags = new ArrayList<>();
        Set<String> uniqueTags = new HashSet<>();
        for (Event event : allEvents) {
            uniqueTags.addAll(event.getTags());
        }
        allTags.addAll(uniqueTags);

        // Initially select all tags
        selectedTags.addAll(allTags);

        // Initially show filtered events
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getActivity(), eventList);
        recyclerView.setAdapter(eventAdapter);

        // Apply initial filter
        filterEvents();

        return view;
    }

    private void showTagFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_tag_filter, null);
        builder.setView(dialogView);

        ListView tagListView = dialogView.findViewById(R.id.tagListView);
        EditText searchBar = dialogView.findViewById(R.id.searchBar);
        Button checkAllButton = dialogView.findViewById(R.id.checkAllButton);
        Button removeAllButton = dialogView.findViewById(R.id.removeAllButton);
        Button applyButton = dialogView.findViewById(R.id.applyButton);

        TagFilterAdapter tagAdapter = new TagFilterAdapter(getActivity(), allTags);
        tagListView.setAdapter(tagAdapter);

        // Restore previous checked states
        boolean[] checkedStates = new boolean[allTags.size()];
        for (int i = 0; i < allTags.size(); i++) {
            checkedStates[i] = selectedTags.contains(allTags.get(i));
        }
        tagAdapter.setCheckedStates(checkedStates);

        // Add TextWatcher to the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter tags as the user types
                tagAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        checkAllButton.setOnClickListener(v -> tagAdapter.checkAll());
        removeAllButton.setOnClickListener(v -> tagAdapter.removeAll());

        AlertDialog dialog = builder.create();
        applyButton.setOnClickListener(v -> {
            selectedTags.clear();
            boolean[] newCheckedStates = tagAdapter.getCheckedStates();
            for (int i = 0; i < newCheckedStates.length; i++) {
                if (newCheckedStates[i]) {
                    selectedTags.add(allTags.get(i));
                }
            }
            filterEvents();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterEvents() {
        eventList.clear();

        for (Event event : allEvents) {
            // Check if the event matches the selected tags (at least one tag must match)
            boolean matchesTag = false;
            if (!selectedTags.isEmpty()) {
                for (String tag : event.getTags()) {
                    if (selectedTags.contains(tag)) {
                        matchesTag = true;
                        break;
                    }
                }
            }

            // Check if the event matches the event type (General or Fifthrow)
            boolean matchesEventType = showFifthrowEvents
                    ? event.getTags().contains("fifthrow")
                    : !event.getTags().contains("fifthrow");

            if (matchesTag && matchesEventType) {
                eventList.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
    }
}