package com.example.heartratealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String toastText = "OOGA BOOGA ALARM";
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

    }
}
