package com.example.heartratealarm.ui.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.EditAlarmActivity;
import com.example.heartratealarm.R;
import com.example.heartratealarm.alarm.Alarm;
import com.example.heartratealarm.alarm.AlarmDatabase;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

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
    private Disposable writerDisposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarms, container, false);
        root.findViewById(R.id.newAlarmButton).setOnClickListener(this);
        // TODO: remove magic button
        root.findViewById(R.id.magicButton).setOnClickListener(this);

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
            case R.id.newAlarmButton:
                Log.d(TAG, "newAlarmButton Pressed, switching to NewAlarm Activity");
                Intent intent = new Intent(requireActivity(), EditAlarmActivity.class);
                startActivity(intent);
                break;
            case R.id.magicButton:
                Toast.makeText(getContext(), "ARE YOU HAPPY DOM? T-MINUS 10 SECONDS", Toast.LENGTH_SHORT).show();
                Single<Alarm> single;
                int alarmID = 101;
                single = Alarm.loadAlarm(alarmID, requireContext());
                readerDisposable = single.observeOn(AndroidSchedulers.mainThread()).subscribe(a -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Calendar.getInstance().getTime());
                    calendar.add(Calendar.SECOND, 10);
                    a.enableAlarm(requireActivity(), calendar);
                }, e -> Log.e(TAG, "Insta-Alarm: ", e));
        }
    }

    // TODO: where ya'll make the list of all alarms
    public void listAlarms() {
        LinearLayout alarmsList = requireView().findViewById(R.id.alarmsList);
        int count = 0;
        final float scale = getResources().getDisplayMetrics().density;
        for (Alarm alarm : alarmList) {
            Log.d(TAG, alarm.toString());
            if (alarm.enabled) {
                alarm.enableAlarm(requireActivity());
            }
            MaterialCardView alarmCard = new MaterialCardView(requireContext());
            LinearLayout cardLayout = new LinearLayout(requireContext());
            cardLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardLayout.setPadding((int) (8 * scale), (int) (16 * scale), 0, (int) (16 * scale));
            cardLayout.setGravity(Gravity.CENTER_VERTICAL);

            SwitchMaterial enabled = new SwitchMaterial(requireContext());
            enabled.setChecked(alarm.enabled);
            enabled.setGravity(Gravity.CENTER_VERTICAL);
            enabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                alarm.enabled = isChecked;
                writerDisposable = alarm.updateAlarm(requireContext(), requireActivity());
                if (!isChecked) {
                    alarm.disableAlarm(requireActivity());
                }
            });
            cardLayout.addView(enabled);

            TextView time = new TextView(requireContext());
            time.setText(DateUtils.formatDateTime(requireContext(), alarm.getUnixTime(), DateUtils.FORMAT_SHOW_TIME));
            time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            time.setPadding((int) (8 * scale), 0, 0, 0);
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
        if (writerDisposable != null && !writerDisposable.isDisposed()) {
            writerDisposable.dispose();
        }
    }
}