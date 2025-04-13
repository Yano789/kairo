package com.example.infosys_1d.Event;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventViewModel extends ViewModel {
    private static final String TAG = "EventViewModel";
    private MutableLiveData<List<Event>> generalEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> fifthrowEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> calendarEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> allEvents = new MutableLiveData<>();

    public LiveData<List<Event>> getGeneralEvents() {
        return generalEvents;
    }

    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }

    public LiveData<List<Event>> getFifthrowEvents() {
        return fifthrowEvents;
    }

    public LiveData<List<Event>> getCalendarEvents() {
        return calendarEvents;
    }

    public void refreshEvents(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            Log.w(TAG, "Cannot refresh events: userEmail is empty");
            calendarEvents.postValue(new ArrayList<>());
            return;
        }
        List<Event> personalEvents = EventRepository.getCalendarEvents(userEmail);
        Log.d(TAG, "Refreshing events for " + userEmail + ": " + personalEvents.size() + " personal events");
        for (Event e : personalEvents) {
            Log.d(TAG, " - " + e.getName() + ", title: " + e.getTitle() + ", tags: " + e.getTags());
        }
        calendarEvents.postValue(personalEvents);
    }

    public void refreshDiscoverableEvents() {
        List<Event> discoverableEvents = new ArrayList<>();
        for (Event event : EventRepository.getGeneralEvents()) {
            discoverableEvents.add(event);
        }
        Log.d(TAG, "Refreshing discoverable events: " + discoverableEvents.size() + " events");
        allEvents.postValue(discoverableEvents);
    }

    public List<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        for (Event e : EventRepository.getGeneralEvents()) {
            allTags.addAll(e.getTags());
        }
        return new ArrayList<>(allTags);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        calendarEvents.postValue(new ArrayList<>());
        Log.d(TAG, "ViewModel cleared, resetting calendarEvents");
    }
}