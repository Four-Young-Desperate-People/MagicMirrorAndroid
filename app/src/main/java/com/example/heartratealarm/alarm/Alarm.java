package com.example.heartratealarm.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.heartratealarm.AlarmReceiver;
import com.example.heartratealarm.MainActivity;
import com.example.heartratealarm.R;
import com.example.heartratealarm.websocket.GenericData;
import com.example.heartratealarm.websocket.WebSocketBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

    @ColumnInfo(name = "alarm_volume")
    public float alarmVolume = 0.5f;

    @ColumnInfo(name = "exercise_path")
    public String exercisePath;

    @ColumnInfo(name = "exercise_volume")
    public float exerciseVolume = 0.5f;

    @ColumnInfo(name = "brightness")
    public int brightness = 50;

    @ColumnInfo(name = "squats")
    public boolean squats = true;

    @ColumnInfo(name = "jumping_jacks")
    public boolean jumpingJacks = true;

    @ColumnInfo(name = "burpees")
    public boolean burpees = true;

    @ColumnInfo(name = "enabled")
    public boolean enabled;

    @ColumnInfo(name = "hr_target")
    public int hrTarget = 90;


    public Alarm() {
        Calendar defaultRun = Calendar.getInstance();
        defaultRun.add(Calendar.DAY_OF_YEAR, 1);
        this.setNextRun(defaultRun);
    }

    private static List<Alarm> searchAlarm(int id, Context context) {
        return AlarmDatabase.getInstance(context).alarmDao().getAlarmByID(id);
    }

    public static Single<Alarm> loadAlarm(int id, Context context) {
        return Single.fromCallable(() -> searchAlarm(id, context)).subscribeOn(Schedulers.io()).map(l -> l.get(0));
    }

    // Main logic that is called when the alarm is running, since called from an intent, static
    // TODO: May be a good idea to have recurrent alarms, we currently just disable the alarm after
    public static void runAlarm(Context context, Intent intent) {
        // Get alarm information from SQL
        int alarmID = intent.getExtras().getInt("ID");
        Log.d(TAG, "runAlarm: Received Alarm ID: " + alarmID);
        Single<Alarm> alarmSingle = loadAlarm(alarmID, context);

        // Actual Alarm Running Happens Here
        Disposable disposable = alarmSingle.observeOn(AndroidSchedulers.mainThread()).subscribe(runningAlarm -> {
            // Check to see if the alarm is actually disabled
            if (!runningAlarm.enabled) {
                return;
            }

            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int OGVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            // TODO: disable volume buttons

            // TODO: fix vibrating
            // Begin Vibrating if Vibrating is on
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (runningAlarm.vibrate) {
                Log.d(TAG, "runAlarm: I GOTTA VIBE");
                vibe.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 1000}, 0));
            }

            // Set up Media Players for Alarm Music
            Uri alarmSong = Uri.parse(runningAlarm.songPath);
            MediaPlayer alarmMp = MediaPlayer.create(context, alarmSong);
            alarmMp.setVolume(runningAlarm.alarmVolume, runningAlarm.alarmVolume);
            alarmMp.setLooping(true);
            alarmMp.setScreenOnWhilePlaying(true);
            MediaPlayer exerciseMp;
            Uri exerciseSong;
            boolean hasExerciseSong;
            if (runningAlarm.exercisePath != null) {
                exerciseSong = Uri.parse(runningAlarm.exercisePath);
                exerciseMp = MediaPlayer.create(context, exerciseSong);
                exerciseMp.setVolume(runningAlarm.exerciseVolume, runningAlarm.exerciseVolume);
                exerciseMp.setLooping(true);
                exerciseMp.setScreenOnWhilePlaying(true);
                hasExerciseSong = true;
                // Else needed to make these variables final lambda safe
            } else {
                hasExerciseSong = false;
                exerciseMp = null;
            }

            // Play Music
            Log.d(TAG, "runAlarm: playing music");
            alarmMp.start();

            // Make a window, TODO: test with lock screen
            WindowManager.LayoutParams
                    p = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myView = inflater.inflate(R.layout.activity_alarm, null);
            Button btnDismiss = myView.findViewById(R.id.alarmDismissButton);

            WebSocketBase ws = new WebSocketBase();
            Gson gson = new Gson();
            AtomicBoolean pinged = new AtomicBoolean(false);
            AtomicBoolean allowDismiss = new AtomicBoolean(false);
            AtomicInteger missedCount = new AtomicInteger(0);

            Runnable stop = () -> {
                if (alarmMp.isPlaying()) {
                    alarmMp.stop();
                }
                if (hasExerciseSong && exerciseMp.isPlaying()) {
                    exerciseMp.stop();
                }
                windowManager.removeView(myView);
                exitAlarm(context);
                vibe.cancel();
                am.setStreamVolume(AudioManager.STREAM_MUSIC, OGVolume, 0);
                // TODO, either cancel next alarm or set next alarm BLARG
                ws.close();
            };

            Disposable messageDisposable = ws.getMessageObservable().observeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                GenericData gd = gson.fromJson(s, GenericData.class);
                if (gd.method.equals("pong")) {
                    missedCount.set(0);
                    pinged.set(false);
                    Log.i(TAG, "ponged");
                } else if (gd.method.equals("quiet_alarm")) {
                    Log.d(TAG, "Got a quiet alarm");
                    Type type = new TypeToken<GenericData<Boolean>>() {
                    }.getType();
                    GenericData<Boolean> shouldWeQuite = gson.fromJson(s, type);
                    // TODO: fix the exercise music volume here
                    if (shouldWeQuite.data) {
                        alarmMp.stop();
                        if (hasExerciseSong) {
                            exerciseMp.start();
                        }
                    } else {
                        if (hasExerciseSong) {
                            exerciseMp.stop();
                        }
                        alarmMp.start();
                    }
                } else if (gd.method.equals("stop_alarm")) {
                    Log.d(TAG, "Got a stop alarm");
                    stop.run();
                }
            }, e -> {
                Log.e(TAG, "Got an err from system websocket", e);
                // duplicate code... sue me
                allowDismiss.set(true);
                btnDismiss.setText("DISMISS");
                btnDismiss.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.purple_500));
            }, () -> {
                Log.i(TAG, "Completed receiving from websocket completed");
            });

            ws.interval(s -> {
                GenericData<Long> gd = new GenericData<>("ping", s);
                if (pinged.get()) {
                    int missed = missedCount.incrementAndGet();
                    Log.i(TAG, String.format("missed ping, current count %d", missed));

                    // we have missed 5 pings. Sever is ignore us or is down. Enable the button.
                    if (missed > 5) {
                        allowDismiss.set(true);
                    }
                }
                pinged.set(true);
                ws.send(gson.toJson(gd));
            });

            // start this whole shebang
            AlarmJson json = new AlarmJson(runningAlarm.brightness, runningAlarm.hrTarget, new boolean[]{runningAlarm.squats, runningAlarm.jumpingJacks, runningAlarm.burpees});
            GenericData<AlarmJson> gd = new GenericData("alarm_start", json);
            ws.send(gson.toJson(gd));

            windowManager.addView(myView, p);

            btnDismiss.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            btnDismiss.setText("GO TO MIRROR AND EXERCISE!");
            btnDismiss.setOnClickListener(v -> {
                if (allowDismiss.get()) {
                    stop.run();
                } else {
                    Log.d(TAG, "runAlarm: User tried to exit, denying");
                }
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
        Log.d(TAG, "enableAlarm: " + getUnixTime());
    }

    //TODO: DELETE CUZ THIS IS DEBUG CODE
    public void enableAlarm(Activity activity, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, id, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "enableAlarm: " + calendar.getTimeInMillis());
    }

    public void disableAlarm(Activity activity) {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra("ID", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public Disposable deleteAlarm(Context context) {
        return Single.just(this).subscribeOn(Schedulers.io()).subscribe(t -> {
            AlarmDao alarmDao = AlarmDatabase.getInstance(context).alarmDao();
            Log.d(TAG, "deleteAlarm: " + t.id);
            alarmDao.delete(this);
        }, e -> {
            Log.e(TAG, "deleteAlarm: ", e);
        });
    }

    public long getUnixTime() {
        Calendar calendar = Calendar.getInstance();
        long currTime = Calendar.getInstance().getTimeInMillis();
        calendar.setTimeInMillis(currTime);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (currTime >= calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTimeInMillis();
    }

    // Saves the alarm to sql
    public Disposable saveAlarm(Context context, Activity activity) {
        return Single.just(this).subscribeOn(Schedulers.io()).subscribe(t -> {
            AlarmDao alarmDao = AlarmDatabase.getInstance(context).alarmDao();
            int currMax = alarmDao.getMaxAlarm();
            if (currMax == 0) {
                t.id = 101;
            } else {
                t.id = currMax + 1;
            }
            Log.d(TAG, "saveAlarm: " + t.toString());
            alarmDao.insert(t);
            t.enableAlarm(activity);
        }, e -> {
            Log.e(TAG, "saveAlarm: ", e);
        });
    }

    // Updates an exiting alarm in sql
    public Disposable updateAlarm(Context context, Activity activity) {
        return Single.just(this).subscribeOn(Schedulers.io()).subscribe(t -> {
            AlarmDao alarmDao = AlarmDatabase.getInstance(context).alarmDao();
            Log.d(TAG, "updateAlarm: " + t.toString());
            alarmDao.update(t);
            t.enableAlarm(activity);
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
                ", squats=" + squats +
                ", jumpingJacks=" + jumpingJacks +
                ", burpees=" + burpees +
                ", enabled=" + enabled +
                ", hrTarget=" + hrTarget +
                '}';
    }
}
