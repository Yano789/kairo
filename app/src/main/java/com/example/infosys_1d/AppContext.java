package com.example.infosys_1d;

import android.content.Context;

public class AppContext {
    private static Context appContext;

    public static void setAppContext(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }
}