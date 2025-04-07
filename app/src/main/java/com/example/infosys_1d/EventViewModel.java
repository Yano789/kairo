package com.example.infosys_1d;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Event>> allEvents = new MutableLiveData<>();
    private final List<String> allTags = new ArrayList<>();

    public EventViewModel(@NonNull Application application) {
        super(application);
        EventRepository.loadDummyEvents(getApplication().getApplicationContext());
        loadEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }

    public List<String> getAllTags() {
        return allTags;
    }

    public void refreshEvents() {
        loadEvents();
    }

    private void loadEvents() {
        // Load from repository or database
        List<Event> events = EventRepository.getEvents();
        allEvents.postValue(events);

        // Update tags
        Set<String> uniqueTags = new HashSet<>();
        for (Event event : events) {
            uniqueTags.addAll(event.getTags());
        }
        allTags.clear();
        allTags.addAll(uniqueTags);
    }
}