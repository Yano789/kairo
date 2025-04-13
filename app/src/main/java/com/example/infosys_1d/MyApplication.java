package com.example.infosys_1d;

import android.app.Application;
import android.util.Log;
import com.example.infosys_1d.Event.EventRepository;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Initializing dummy events");
        EventRepository.loadDummyEvents(getApplicationContext());
    }
}