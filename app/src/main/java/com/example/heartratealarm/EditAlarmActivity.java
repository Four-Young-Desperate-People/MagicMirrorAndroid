package com.example.heartratealarm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class EditAlarmActivity extends AppCompatActivity {
    public static final int REQ_PICK_AUDIO = 10001;
    private static final String TAG = "EditAlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        Button btnRingtone = (Button) this.findViewById(R.id.selectRingtone);

        btnRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_AUDIO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_PICK_AUDIO || resultCode != RESULT_OK || data == null){
            return;
        }
        Uri uri = data.getData();
        String songPath = uri.getPath();

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songPath);
        } catch (IOException e) {
            Log.e(TAG, "onActivityResult: " + e.getMessage());
        }
        mediaPlayer.start();

    }
}