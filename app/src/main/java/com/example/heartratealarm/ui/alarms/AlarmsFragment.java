package com.example.heartratealarm.ui.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.Alarm;
import com.example.heartratealarm.EditAlarmActivity;
import com.example.heartratealarm.R;

import java.util.Calendar;

public class AlarmsFragment extends Fragment implements View.OnClickListener {

    private AlarmsViewModel alarmsViewModel;

    public static final String TAG = "AlarmsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        root.findViewById(R.id.testAlarmButton).setOnClickListener(this);
        root.findViewById(R.id.newAlarmButton).setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
//      TODO: Android says that Resource IDs will be non-final, recommends using if-else?
        switch (view.getId()) {
            case R.id.testAlarmButton:
                Context context = requireActivity().getApplicationContext();
                Calendar alarmTime = Calendar.getInstance();
                alarmTime.add(Calendar.SECOND, 10);
                String toastText = "Setting alarm for " + alarmTime.getTime().toString();
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
                Alarm alarm = new Alarm(alarmTime);
                alarm.setAlarm(requireActivity());
                break;
            case R.id.newAlarmButton:
                Log.d(TAG, "newAlarmButton Pressed, switching to NewAlarm Activity");
                Intent intent = new Intent(requireActivity(), EditAlarmActivity.class);
                startActivity(intent);
                break;

        }

    }
}