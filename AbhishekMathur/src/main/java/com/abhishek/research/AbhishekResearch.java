package com.abhishek.research;

import android.app.Application;

public class AbhishekResearch extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());
    }
}
