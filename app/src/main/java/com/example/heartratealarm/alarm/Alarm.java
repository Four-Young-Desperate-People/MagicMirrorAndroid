package com.example.heartratealarm.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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

import com.example.heartratealarm.AlarmReceiver;
import com.example.heartratealarm.MainActivity;
import com.example.heartratealarm.R;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Entity(tableName = "alarms")
public class Alarm {
    private static final String TAG = "Alarm";

    @PrimaryKey
    @NonNull
    public int id;

    @ColumnInfo(name = "hour_of_day")
    public int hourOfDay;

    @ColumnInfo(name = "minute")
    public int minute;

    @ColumnInfo(name = "vibrate")
    public boolean vibrate = false;

    @ColumnInfo(name = "song_path")
    public String songPath;

    @ColumnInfo(name = "alarmVolume")
    public int alarmVolume = 50;

    @ColumnInfo(name = "exercise_path")
    public String exercisePath;

    @ColumnInfo(name = "exerciseVolume")
    public int exerciseVolume = 50;

    @ColumnInfo(name = "brightness")
    public int brightness = 50;

    @ColumnInfo(name = "exercise")
    public int exercise = 0;

    @ColumnInfo(name = "enabled")
    public boolean enabled;


    public Alarm() {
        Calendar defaultRun = Calendar.getInstance();
        defaultRun.add(Calendar.DAY_OF_YEAR, 1);
        this.setNextRun(defaultRun);
    }

    private static List<Alarm> searchAlarm(int id, Context context) {
        return AlarmDatabase.getInstance(context).alarmDao().getAlarmByID(id);
    }

    // TODO: There is a discrepancy between loadAlarm and loadAll Alarms here
    public static Single<Alarm> loadAlarm(int id, Context context) {
        return Single.fromCallable(() -> searchAlarm(id, context)).subscribeOn(Schedulers.io()).map(l -> l.get(0));
    }

    // Main logic that is called when the alarm is running, since called from an intent, static
    public static void runAlarm(Context context, Intent intent) {
        // TODO DOM PUT WEBSOCKET HERE FOR FULL PATH
        Log.d(TAG, "runAlarm: " + intent.getExtras().keySet());
        int alarmID = intent.getExtras().getInt("ID");
        // TODO: get rid of this debug text
        Toast.makeText(context, "ALARM ID" + alarmID, Toast.LENGTH_LONG).show();

        Log.d(TAG, "runAlarm: Received Alarm ID: " + alarmID);


        // TODO: get alarm information from SQL
        Log.d(TAG, "runAlarm: Building Alarm");
        Single<Alarm> alarmSingle = loadAlarm(alarmID, context);
        Disposable disposable = alarmSingle.observeOn(AndroidSchedulers.mainThread()).subscribe(runningAlarm -> {
            // Check to see if the alarm is actually disabled
            if (!runningAlarm.enabled){
                return;
            }
            // Set up Media Players for Alarm Music
            Uri alarmSong = Uri.parse(runningAlarm.songPath);
            MediaPlayer alarmMp = MediaPlayer.create(context, alarmSong);
            alarmMp.setVolume(runningAlarm.alarmVolume, runningAlarm.alarmVolume);
            alarmMp.setLooping(true);
            alarmMp.setScreenOnWhilePlaying(true);
            MediaPlayer exerciseMp;
            Uri exerciseSong;
            boolean hasExerciseSong = false;
            if (runningAlarm.exercisePath != null) {
                exerciseSong = Uri.parse(runningAlarm.exercisePath);
                exerciseMp = MediaPlayer.create(context, exerciseSong);
                exerciseMp.setVolume(runningAlarm.exerciseVolume, runningAlarm.alarmVolume);
                exerciseMp.setLooping(true);
                exerciseMp.setScreenOnWhilePlaying(true);
                hasExerciseSong = true;
            }

            // Play Music
            Log.d(TAG, "runAlarm: playing music");
            alarmMp.start();

            // Make a window
            // TODO: this doesn't work for the lock screen just yet
            WindowManager.LayoutParams p = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.activity_alarm, null, false);
            Button btnDismiss = myView.findViewById(R.id.alarmDismissButton);
            windowManager.addView(myView, p);
            btnDismiss.setOnClickListener(v -> {
                // TODO: add logic for disabling dismiss Button
                if (false) {
                    Toast.makeText(context, "HAHA GET REKT", Toast.LENGTH_SHORT).show();
                }
                alarmMp.stop();
                windowManager.removeView(myView);
                exitAlarm(context);
            });

        }, e -> {
            Log.e(TAG, "Alarm Load: ", e);
        });


    }

    static public void exitAlarm(Context context) {
        Log.d(TAG, "Alarm: Exiting alarm");
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void enableAlarm(Activity activity) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, id, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getUnixTime(), pendingIntent);
    }

    public long getUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long currTime = Calendar.getInstance().getTimeInMillis();
        calendar.setTimeInMillis(currTime);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (currTime >= calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis();
    }

    // Saves the alarm to sql
    public Disposable saveAlarm(Context context) {
        return Single.just(this).subscribeOn(Schedulers.io()).subscribe(t -> {
            AlarmDao alarmDao = AlarmDatabase.getInstance(context).alarmDao();
            int currMax = alarmDao.getMaxAlarm();
            if (currMax == 0) {
                this.id = 101;
            } else {
                this.id = currMax + 1;
            }
            Log.d(TAG, "saveAlarm: " + t.toString());
            alarmDao.insert(t);
        }, e -> {
            Log.e(TAG, "saveAlarm: ", e);
        });
    }

    // Updates an exiting alarm in sql
    public Disposable updateAlarm(Context context) {
        return Single.just(this).subscribeOn(Schedulers.io()).subscribe(t -> {
            AlarmDao alarmDao = AlarmDatabase.getInstance(context).alarmDao();
            Log.d(TAG, "updateAlarm: " + t.toString());
            alarmDao.update(t);
        }, e -> {
            Log.e(TAG, "updateAlarm: ", e);
        });
    }

    public void setNextRun(Calendar nextRun) {
        hourOfDay = nextRun.get(Calendar.HOUR_OF_DAY);
        minute = nextRun.get(Calendar.MINUTE);
    }

    public void setNextRun(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }


    public String timeToRun() {
        long currTime = Calendar.getInstance().getTimeInMillis();
        long diff = getUnixTime() - currTime;
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long mins = TimeUnit.MILLISECONDS.toMinutes(diff) - TimeUnit.HOURS.toMinutes(hours);
        if (hours == 0 && mins == 0) {
            return "under a minute";
        }
        return String.format("%d Hours and %d minutes", hours, mins);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", runTime=" + hourOfDay + ":" + minute +
                ", vibrate=" + vibrate +
                ", songPath='" + songPath + '\'' +
                ", alarmVolume=" + alarmVolume +
                ", exercisePath='" + exercisePath + '\'' +
                ", exerciseVolume=" + exerciseVolume +
                ", brightness=" + brightness +
                ", enabled=" + enabled +
                '}';
    }
}
