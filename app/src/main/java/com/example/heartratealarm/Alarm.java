package com.example.heartratealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class Alarm {
    Calendar nextRun;
    int ID;


    public Alarm(Calendar nextRun) {
        this.nextRun = nextRun;
    }

    void runAlarm() {
//        Does 3 things, launches the app and opens the alarm page

    }

    public void setAlarm(Activity activity) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, 0);

        // Needs API above level 19, which is KitKat, which was released in 2013, added an androidx.annotation.RequiresApi
        // Probably needs to update something so that we don't need this annotation in the future
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextRun.getTimeInMillis(), pendingIntent);
    }

}
