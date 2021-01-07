package com.example.heartratealarm.ui.alarms;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.Alarm;
import com.example.heartratealarm.R;

import java.util.Calendar;

public class AlarmsFragment extends Fragment implements View.OnClickListener{

    private AlarmsViewModel alarmsViewModel;

    public static final String TAG = "AlarmsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        Button btnTestAlarm = (Button) root.findViewById(R.id.testAlarmButton);
        btnTestAlarm.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        Context context = requireActivity().getApplicationContext();
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.add(Calendar.SECOND, 30);
        String toastText = "Setting alarm for " + alarmTime.getTime().toString();
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
        Alarm alarm = new Alarm(alarmTime);
    }
}