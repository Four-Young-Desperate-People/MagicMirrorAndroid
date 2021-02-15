package com.example.heartratealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;

import java.util.Calendar;

public class Alarm{
    Calendar nextRun;
    int ID;
    boolean vibrate;
    Ringtone r;

    public Alarm(Calendar nextRun) {
        this.nextRun = nextRun;
    }

    public void setAlarm(Activity activity) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextRun.getTimeInMillis(), pendingIntent);
    }

}
