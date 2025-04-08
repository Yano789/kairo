package com.example.infosys_1d.Event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventViewModel extends ViewModel {
    private MutableLiveData<List<Event>> generalEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> fifthrowEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> calendarEvents = new MutableLiveData<>();  // Add this line

    // Getters for LiveData
    public LiveData<List<Event>> getGeneralEvents() {
        return generalEvents;
    }

    public LiveData<List<Event>> getFifthrowEvents() {
        return fifthrowEvents;
    }

    public LiveData<List<Event>> getCalendarEvents() {
        return calendarEvents;  // Return the calendar events LiveData
    }

    // Method to refresh events (called when data changes)
    public void refreshEvents() {
        generalEvents.setValue(EventRepository.getGeneralEvents());

        // Filter the fifthrow events based on tags
        List<Event> filteredFifthrowEvents = new ArrayList<>();
        for (Event event : EventRepository.getGeneralEvents()) {
            if (event.getTags().contains("fifthrow")) {
                filteredFifthrowEvents.add(event);
            }
        }
        fifthrowEvents.setValue(filteredFifthrowEvents);

        // Update calendar events
        calendarEvents.setValue(EventRepository.getCalendarEvents());
    }

    // Get all tags from events
    public List<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        for (Event e : EventRepository.getGeneralEvents()) {
            allTags.addAll(e.getTags());
        }
        return new ArrayList<>(allTags);
    }
}
