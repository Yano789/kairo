package com.example.infosys_1d.Discovery;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

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
import com.example.infosys_1d.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button toggleEventTypeButton;
    private ImageButton filterButton;

    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private final List<Event> displayedEvents = new ArrayList<>();
    private final Set<String> selectedTags = new HashSet<>();
    private boolean showFifthrowEvents = false;

    private EventAdapter.OnEventActionListener onEventActionListener;

    private View emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class); // Shared ViewModel
        // Load the dummy data
        EventRepository.loadDummyEvents(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Your other initialization code for RecyclerView or any other UI elements
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        emptyView = view.findViewById(R.id.emptyView);

        // Only set visibility for now; adapter will be handled in setupRecyclerView
        emptyView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the views you need to set up the button listeners
        toggleEventTypeButton = view.findViewById(R.id.toggleEventTypeButton);
        filterButton = view.findViewById(R.id.filterButton);

        // Set up the button listeners
        if (toggleEventTypeButton != null) {
            toggleEventTypeButton.setOnClickListener(v -> {
                showFifthrowEvents = !showFifthrowEvents;
                toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
                eventViewModel.refreshEvents();  // Refresh events when toggling between types
            });

        }

        if (filterButton != null) {
            filterButton.setOnClickListener(v -> showTagFilterDialog());
        }

        // Set up the RecyclerView
        setupRecyclerView();

        // Set up the observers
        setupObservers();
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        toggleEventTypeButton = view.findViewById(R.id.toggleEventTypeButton);
        filterButton = view.findViewById(R.id.filterButton);
    }

    private void setupRecyclerView() {

        eventAdapter = new EventAdapter(requireContext(), displayedEvents,
                R.layout.discovery_item_event, new EventAdapter.OnEventActionListener() {
            @Override
            public void onAddToCalendar(Event event) {
                // Move to calendar and refresh
                EventRepository.moveToCalendar(event);
                eventViewModel.refreshEvents();  // Automatically updates both fragments
            }

            @Override
            public void onRemoveFromCalendar(Event event) {
                // leave empty if not used
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(eventAdapter);
    }

    private void setupObservers() {
        eventViewModel.getAllEvents().observe(getViewLifecycleOwner(), this::filterAndDisplayEvents);
        eventViewModel.refreshEvents();
    }


    private void setupButtonListeners() {
        toggleEventTypeButton.setOnClickListener(v -> {
            showFifthrowEvents = !showFifthrowEvents;
            toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
            eventViewModel.refreshEvents();
        });

        filterButton.setOnClickListener(v -> showTagFilterDialog());
    }

    private void filterAndDisplayEvents(List<Event> allEvents) {
        displayedEvents.clear();

        for (Event event : allEvents) {
            boolean isFifthrow = event.getTags().contains("fifthrow");
            boolean matchesType = (showFifthrowEvents == isFifthrow);
            boolean matchesTags = selectedTags.isEmpty() || containsAny(event.getTags(), selectedTags);

            if (matchesType && matchesTags) {
                displayedEvents.add(event);
            }
        }

        // Sort by start time (earliest first)
        displayedEvents.sort(Comparator.comparingLong(Event::getStartTime));

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
            eventViewModel.refreshEvents();
            dialog.dismiss();
        });

        dialog.show();
    }
}
