package com.example.heartratealarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Date;

public class EditAlarmActivity extends AppCompatActivity {
    public static final int REQ_PICK_ALARM = 10001;
    public static final int REQ_PICK_EXERCISE = 10002;
    public static final int REQ_STORAGE_PERMS = 10003;
    private static final String TAG = "EditAlarmActivity";
    Alarm alarm;
    AlarmDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        db = AlarmDatabase.getInstance(getApplicationContext());
        AlarmDao alarmDao = db.alarmDao();

        this.alarm = new Alarm();
        Bundle alarmInfo = getIntent().getExtras();
        String alarmID;
        if (alarmInfo != null) {
            Log.d(TAG, "Editing Alarm");
            alarmID = alarmInfo.getString("id");
            alarm.loadAlarm(alarmID);
            //TODO: Update Views to reflect loaded alarm
        }

        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), perms[0]) != PackageManager.PERMISSION_GRANTED) {
            Toast explanation = Toast.makeText(getApplicationContext(), "We need access to your files to play music!", Toast.LENGTH_LONG);
            explanation.show();
            ActivityCompat.requestPermissions(this, perms, REQ_STORAGE_PERMS);
        }

        TextView timeCountdown = this.findViewById(R.id.timeToRun);
        timeCountdown.setText("Ring in: " + alarm.timeToRun());

        Button btnAlarmSong = this.findViewById(R.id.alarmMusicButton);
        btnAlarmSong.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_PICK_ALARM);
        });

        Button btnExerciseSong = this.findViewById(R.id.exerciseMusicButton);
        btnExerciseSong.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_PICK_EXERCISE);
        });

        Button btnSave = this.findViewById(R.id.saveButton);
        btnSave.setOnClickListener(v -> {
            if (alarm.songPath == null) {
                Toast.makeText(getApplicationContext(), "No Song Selected", Toast.LENGTH_LONG).show();
            } else {
                alarm.enabled = true;
                alarm.saveAlarm(getApplicationContext());
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        Slider sliderAlarmVolume = this.findViewById(R.id.volumeSlider);
        sliderAlarmVolume.addOnChangeListener((slider, value, fromUser) -> alarm.alarmVolume = (int) value);

        SwitchMaterial vibrateSwitch = this.findViewById(R.id.vibrateSwitch);
        vibrateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> alarm.vibrate = isChecked);

        TimePicker timePicker = this.findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            alarm.setNextRun(Alarm.findNextRun(hourOfDay, minute));
            timeCountdown.setText("Ring in: " + alarm.timeToRun());
        });

        Slider sliderBrightness = this.findViewById(R.id.volumeSlider);
        sliderBrightness.addOnChangeListener((slider, value, fromUser) -> alarm.brightness = (int) value);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        String songPath = uri.getPath();

        String path = null;
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        path = "file://" + path;

        if (requestCode == REQ_PICK_ALARM) {
            alarm.songPath = path;
            Log.d(TAG, "Alarm Song: " + path);
            // TODO: LIST SONG NAME
        }
        if (requestCode == REQ_PICK_EXERCISE) {
            alarm.exercisePath = path;
            Log.d(TAG, "Exercise Song: " + path);
            // TODO: LIST SONG NAME
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE_PERMS) {
            if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(EditAlarmActivity.this, perms, REQ_STORAGE_PERMS);
                Toast explanation = Toast.makeText(getApplicationContext(), "Unable to continue making alarm due to permissions", Toast.LENGTH_LONG);
                explanation.show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }
}