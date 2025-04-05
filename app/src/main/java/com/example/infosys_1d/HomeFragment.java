package com.example.infosys_1d;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    // UI Components
    private RecyclerView recyclerView;
    private Button toggleEventTypeButton;
    private ImageButton filterButton;

    // Data Components
    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;
    private final List<Event> displayedEvents = new ArrayList<>();
    private final Set<String> selectedTags = new HashSet<>();
    private boolean showFifthrowEvents = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setupViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupObservers();
        setupButtonListeners();
    }

    private void setupViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        toggleEventTypeButton = view.findViewById(R.id.toggleEventTypeButton);
        filterButton = view.findViewById(R.id.filterButton);
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(requireContext(), displayedEvents, R.layout.discovery_item_event);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(eventAdapter);
    }

    private void setupObservers() {
        eventViewModel.getAllEvents().observe(getViewLifecycleOwner(), events -> {
            filterAndDisplayEvents(events);
        });
    }

    private void setupButtonListeners() {
        toggleEventTypeButton.setOnClickListener(v -> {
            showFifthrowEvents = !showFifthrowEvents;
            toggleEventTypeButton.setText(showFifthrowEvents ? "Fifthrow" : "General");
            eventViewModel.refreshEvents(); // Triggers new filtering
        });

        filterButton.setOnClickListener(v -> showTagFilterDialog());
    }

    private void filterAndDisplayEvents(List<Event> allEvents) {
        displayedEvents.clear();

        for (Event event : allEvents) {
            // Check event type filter
            boolean matchesType = showFifthrowEvents == event.getTags().contains("fifthrow");

            // Check tag filter
            boolean matchesTags = selectedTags.isEmpty() ||
                    containsAny(event.getTags(), selectedTags);

            if (matchesType && matchesTags) {
                displayedEvents.add(event);
            }
        }

        eventAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private boolean containsAny(List<String> eventTags, Set<String> selectedTags) {
        for (String tag : eventTags) {
            if (selectedTags.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    private void updateEmptyView() {
        View rootView = getView();
        if (rootView != null) {
            boolean showEmptyView = displayedEvents.isEmpty();
            rootView.findViewById(R.id.emptyView).setVisibility(
                    showEmptyView ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(
                    showEmptyView ? View.GONE : View.VISIBLE);
        }
    }

    private void showTagFilterDialog() {
        List<String> allTags = eventViewModel.getAllTags();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_tag_filter, null);
        builder.setView(dialogView);

        // Setup dialog components
        ListView tagListView = dialogView.findViewById(R.id.tagListView);
        EditText searchBar = dialogView.findViewById(R.id.searchBar);
        Button applyButton = dialogView.findViewById(R.id.applyButton);

        TagFilterAdapter tagAdapter = new TagFilterAdapter(requireActivity(), allTags);
        tagListView.setAdapter(tagAdapter);

        // Set initial checked states
        boolean[] checkedStates = new boolean[allTags.size()];
        for (int i = 0; i < allTags.size(); i++) {
            checkedStates[i] = selectedTags.contains(allTags.get(i));
        }
        tagAdapter.setCheckedStates(checkedStates);

        // Search functionality
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
            eventViewModel.refreshEvents(); // Triggers new filtering
            dialog.dismiss();
        });

        dialog.show();
    }
}