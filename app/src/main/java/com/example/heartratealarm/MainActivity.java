package com.example.heartratealarm;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_alarms, R.id.navigation_smart_mirror, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);


        if (!Settings.canDrawOverlays(getApplicationContext())) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_host_fragment), "We need access to draw over applications for the alarm to work!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Settings", new GoToSettings());
            snackbar.show();
        }

//        Single.just(getApplicationContext()).subscribeOn(Schedulers.io()).subscribe(muhContext ->{
//            Log.d(TAG, "onCreate: I AM A BREAKPOINT FOR DEBUGGING THE DB");
//        });
    }

    public class GoToSettings implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }
    }

}