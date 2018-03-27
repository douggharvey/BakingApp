package com.douglasharvey.bakingapp.application;

import android.app.Application;

import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class BakingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}


