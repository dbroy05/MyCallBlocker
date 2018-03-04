package com.diby.mycallblocker;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;

import com.diby.mycallblocker.di.AppInjector;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;

/**
 * Main application to inject all DI modules and components namely Activity and BroadcastReceiver
 */

public class MyCallBlockerApp extends Application implements HasActivityInjector, HasBroadcastReceiverInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInjector.init(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverInjector;
    }
}
