package com.example.heartratealarm;

import android.app.AlarmManager;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

// TODO: Extending Fragment sounds kinda ugly
public class Alarm extends Fragment {
    Calendar nextRun;

    public Alarm(Calendar nextRun) {
        this.nextRun = nextRun;
    }

    void runAlarm(){
//        Does 3 things, launches the app and opens the alarm page

    }

    void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
    }

}
