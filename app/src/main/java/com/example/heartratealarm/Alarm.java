package com.example.heartratealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

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
    @ColumnInfo(name = "alarmVolume")
    public int alarmVolume;
    @ColumnInfo(name = "exercise_path")
    public String exercisePath;
    @ColumnInfo(name = "exerciseVolume")
    public int exerciseVolume;
    @ColumnInfo(name = "enabled")
    public boolean enabled;


    public Alarm() {
        // TODO: change this to check the SQL, make id an int?
        id = UUID.randomUUID().toString();

    }

    // TODO:
    public void loadAlarm(String id){

    }

    // Main logic that is called when the alarm is running, since called from an intent, static
    static public void runAlarm(Context context, Intent intent) {
        // TODO: get rid of this debug text
        Toast.makeText(context, "ALARM", Toast.LENGTH_SHORT).show();

        String alarmID = intent.getExtras().getString(("ID"));
        Log.d(TAG, "runAlarm: Received Alarm ID: " + alarmID);

        // TODO: get alarm information from SQL
        Log.d(TAG, "runAlarm: Searching for alarm...");

        // Make a window
        // TODO: this doesn't work for the lock screen just yet
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.activity_alarm, null, false);
        Button butt = myView.findViewById(R.id.alarmDismissButton);
        windowManager.addView(myView, p);
        butt.setOnClickListener(v -> {
            windowManager.removeView(myView);
            dismissAlarm(context);
        });
    }

    static public void dismissAlarm(Context context) {
        Log.d(TAG, "Alarm: Dismissing alarm");
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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

    public void setNextRun(Calendar nextRun) {
        this.nextRun = nextRun.getTimeInMillis();
    }

    public boolean isVibrate() {
        return vibrate;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + id + '\'' +
                ", nextRun=" + nextRun +
                ", vibrate=" + vibrate +
                ", songPath='" + songPath + '\'' +
                ", alarmVolume=" + alarmVolume +
                ", exercisePath='" + exercisePath + '\'' +
                ", exerciseVolume=" + exerciseVolume +
                ", enabled=" + enabled +
                '}';
    }
}
