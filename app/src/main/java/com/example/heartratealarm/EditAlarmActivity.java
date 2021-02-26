package com.example.heartratealarm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EditAlarmActivity extends AppCompatActivity {
    public static final int REQ_PICK_AUDIO = 10001;
    public static final int REQ_PERMS = 10002;
    private static final String TAG = "EditAlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);

        Button btnRingtone = this.findViewById(R.id.selectRingtone);
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getApplicationContext(), perms[0]) != PackageManager.PERMISSION_GRANTED) {
            Toast explanation = Toast.makeText(getApplicationContext(), "We need access to your files to play music!", Toast.LENGTH_LONG);
            explanation.show();
            ActivityCompat.requestPermissions(EditAlarmActivity.this, perms, REQ_PERMS);
        }
        btnRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_AUDIO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQ_PICK_AUDIO || data == null) {
            return;
        }
        Uri uri = data.getData();
        String songPath = uri.getPath();

        String path = null;
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        //Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        Uri GoodUri = Uri.parse("file://" + path);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, GoodUri);
        mediaPlayer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMS) {
            if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(EditAlarmActivity.this, perms, REQ_PERMS);
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