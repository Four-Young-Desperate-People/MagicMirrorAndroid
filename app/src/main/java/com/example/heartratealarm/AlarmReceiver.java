package com.example.heartratealarm;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.heartratealarm.alarm.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Alarm Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Alarm Received");
        Alarm.runAlarm(context, intent);
    }

}
