package com.example.heartratealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.UUID;

@Entity(tableName = "alarms")
public class Alarm {
    private static final String TAG = "Alarm";
    @PrimaryKey
    @NonNull
    public String id;
    @ColumnInfo(name = "next_run")
    public long nextRun;
    @ColumnInfo(name = "vibrate")
    public boolean vibrate;
    @ColumnInfo(name = "song_path")
    public String songPath;
    @ColumnInfo(name = "enabled")
    public boolean enabled;


    public Alarm() {
        // TODO: change this to check the SQL and make id an int
        id = UUID.randomUUID().toString();

    }

    public void enableAlarm(Activity activity) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextRun, pendingIntent);
    }

    // Saves the alarm to sql
    public void saveAlarm() {

    }

    // Main logic that is called when the alarm is running, since called from an intent, static
    static public void runAlarm(Context context, Intent intent) {
        String alarmID = intent.getExtras().getString(("ID"));
        Log.d(TAG, "runAlarm: Received Alarm ID: " + alarmID);
        // TODO: get alarm information from SQL
        Log.d(TAG, "runAlarm: Searching for alarm...");

        // TODO: test the screen swap
        PackageManager packageManager = context.getPackageManager();
        Intent goToAlarmScreen = new Intent(context, AlarmActivity.class);
        goToAlarmScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(goToAlarmScreen);
    }

    public void setNextRun(Calendar nextRun) {
        this.nextRun = nextRun.getTimeInMillis();
    }

    public boolean isVibrate() {
        return vibrate;
    }

}
