package com.example.heartratealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.heartratealarm.alarm.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm.runAlarm(context, intent);
    }

}
