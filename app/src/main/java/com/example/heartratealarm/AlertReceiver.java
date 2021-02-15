package com.example.heartratealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String toastText = "OOGA BOOGA ALARM";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
        PackageManager packageManager = context.getPackageManager();
        Intent goToAlarmScreen = new Intent(context, AlarmActivity.class);
        goToAlarmScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(goToAlarmScreen);
    }
}
