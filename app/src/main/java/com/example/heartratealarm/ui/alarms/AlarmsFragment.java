package com.example.heartratealarm.ui.alarms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.Alarm;
import com.example.heartratealarm.AlarmDatabase;
import com.example.heartratealarm.EditAlarmActivity;
import com.example.heartratealarm.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlarmsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AlarmsFragment";
    private AlarmsViewModel alarmsViewModel;
    private List<Alarm> alarmList;
    private Disposable readerDisposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        root.findViewById(R.id.testAlarmButton).setOnClickListener(this);
        root.findViewById(R.id.newAlarmButton).setOnClickListener(this);

        // populate our list of alarms
        readerDisposable = Single.just(requireContext()).subscribeOn(Schedulers.io()).map(c -> {
            return AlarmDatabase.getInstance(c).alarmDao().getAll();
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(l -> {
            alarmList = l;
            listAlarms();
        }, e -> {
            Log.d(TAG, "onCreateView: ");
        });

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
                Alarm alarm = new Alarm();
                alarm.setNextRun(alarmTime);
                alarm.enableAlarm(requireActivity());
                break;
            case R.id.newAlarmButton:
                Log.d(TAG, "newAlarmButton Pressed, switching to NewAlarm Activity");
                Intent intent = new Intent(requireActivity(), EditAlarmActivity.class);
                startActivity(intent);
                break;
        }
    }

    // TODO: where ya'll fuckin makin the list of all alarms
    public void listAlarms() {
        LinearLayout alarmsList = requireView().findViewById(R.id.alarmsList);
        int count = 0;
        final float scale = getResources().getDisplayMetrics().density;
        for (Alarm alarm : alarmList) {
            Log.d(TAG, alarm.toString());
            MaterialCardView alarmCard = new MaterialCardView(requireContext());
            LinearLayout cardLayout = new LinearLayout(requireContext());
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding((int) (8 * scale), (int) (16 * scale), 0, (int) (16 * scale));
            cardLayout.setGravity(Gravity.CENTER_VERTICAL);
            CheckBox enabled = new CheckBox(requireContext());
            enabled.setActivated(alarm.enabled);
            TextView time = new TextView(requireContext());
            time.setText(DateUtils.formatDateTime(requireContext(), alarm.nextRun, DateUtils.FORMAT_SHOW_TIME));
            time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            time.setPadding((int) (8 * scale), 0, 0, 0);
            cardLayout.addView(enabled);
            cardLayout.addView(time);
            alarmCard.addView(cardLayout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, (int) (8 * scale));
            alarmCard.setLayoutParams(params);
            alarmCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + alarm.id + " clicked");
                    Intent intent = new Intent(requireActivity(), EditAlarmActivity.class);
                    intent.putExtra("id", alarm.id);
                    startActivity(intent);
                }
            });
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) alarmCard.getLayoutParams();
            alarmsList.addView(alarmCard);
            count++;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (readerDisposable != null && !readerDisposable.isDisposed()) {
            readerDisposable.dispose();
        }
    }
}