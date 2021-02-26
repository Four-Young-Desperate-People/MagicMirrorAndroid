package com.example.heartratealarm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // First-time database setup
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = sharedPreferences.getBoolean("FIRST_RUN", true);
        if (isFirstRun) {
            Log.d(TAG, "Database: Running first-time setup");
            Room.databaseBuilder(this, AlarmDatabase.class, "alarm-database").build();
            sharedPreferences.edit().putBoolean("FIRST_RUN", false).apply();
        } else {
            Log.d(TAG, "Database: NOT first time, skipping setup");
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_alarms, R.id.navigation_smart_mirror, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
//        Button btnInstantAlarm =(Button)findViewById(R.id.instantAlarmButton);
//        btnInstantAlarm.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Log.e(TAG, "onClick: HELLO WORLD");
//            }
//        });
//        startActivity(new Intent(MainActivity.this, AlarmActivity.class));
    }

}