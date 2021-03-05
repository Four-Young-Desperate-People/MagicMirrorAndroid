package com.example.heartratealarm.ui.smartMirror;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.R;
import com.example.heartratealarm.mirror_ui_settings.MagicMirrorUISettings;

public class SmartMirrorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SmartMirrorSettings";
    MagicMirrorUISettings settings = new MagicMirrorUISettings();
    private SmartMirrorViewModel smartMirrorViewModel = new SmartMirrorViewModel();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        smartMirrorViewModel =
                new ViewModelProvider(this).get(SmartMirrorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_smart_mirror, container, false);
        root.findViewById(R.id.cardTopLeft).setOnClickListener(this);
        root.findViewById(R.id.cardTopCenter).setOnClickListener(this);
        root.findViewById(R.id.cardTopRight).setOnClickListener(this);
        root.findViewById(R.id.cardMiddleCenter).setOnClickListener(this);
        root.findViewById(R.id.cardBottomLeft).setOnClickListener(this);
        root.findViewById(R.id.cardBottomCenter).setOnClickListener(this);
        root.findViewById(R.id.cardBottomRight).setOnClickListener(this);
        root.findViewById(R.id.btnSaveMirror).setOnClickListener(this);
        root.findViewById(R.id.btnMirrorSync).setOnClickListener(this);
       
        populate(root);
        return root;
    }

    void populate(View view) {
        TextView topLeft = view.findViewById(R.id.textTopLeft);
        topLeft.setText(settings.getModule(MagicMirrorUISettings.Position.TOP_LEFT));

        TextView topCenter = view.findViewById(R.id.textTopCenter);
        topCenter.setText(settings.getModule(MagicMirrorUISettings.Position.TOP_CENTER));

        TextView topRight = view.findViewById(R.id.textTopRight);
        topRight.setText(settings.getModule(MagicMirrorUISettings.Position.TOP_RIGHT));

        TextView middleCenter = view.findViewById(R.id.textMiddleCenter);
        middleCenter.setText(settings.getModule(MagicMirrorUISettings.Position.MIDDLE_CENTER));

        TextView bottomLeft = view.findViewById(R.id.textBottomLeft);
        bottomLeft.setText(settings.getModule(MagicMirrorUISettings.Position.BOTTOM_LEFT));

        TextView bottomCenter = view.findViewById(R.id.textBottomCenter);
        bottomCenter.setText(settings.getModule(MagicMirrorUISettings.Position.BOTTOM_CENTER));

        TextView bottomRight = view.findViewById(R.id.textBottomRight);
        bottomRight.setText(settings.getModule(MagicMirrorUISettings.Position.BOTTOM_RIGHT));

        Log.d(TAG, "populate: DONE");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMirrorSync) {
            // TODO: for testing, please remove
            String json = "{\"clock\":{\"position\":\"top_left\",\"visible\":\"false\"},\"compliments\":{\"position\":\"middle_center\",\"visible\":\"true\"},\"currentweather\":{\"position\":\"top_center\",\"visible\":\"false\"},\"newsfeed\":{\"position\":\"top_right\",\"visible\":\"false\"},\"weatherforecast\":{\"position\":\"bottom_left\",\"visible\":\"true\"}}";
            settings.fromJson(json);
            populate(v.getRootView());
            return;
        }
        if (v.getId() == R.id.btnSaveMirror) {
            String json = settings.toJson();
            // TODO: YEET TO MIRROR
            Log.d(TAG, "Save Mirror: " + json);
            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            return;
        }
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        for (MagicMirrorUISettings.Module module : MagicMirrorUISettings.Module.values()) {
            menu.add(MagicMirrorUISettings.moduleToString(module));
        }
        menu.add("None");
        popup.show();
        switch (v.getId()) {
            case R.id.cardTopLeft:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Top Left: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.TOP_LEFT);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardTopCenter:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Top Center: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.TOP_CENTER);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardTopRight:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Top Right: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.TOP_RIGHT);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardMiddleCenter:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Middle Center: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.MIDDLE_CENTER);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardBottomLeft:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Bottom Left: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.BOTTOM_LEFT);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardBottomCenter:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Bottom Center: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.BOTTOM_CENTER);
                    populate(v.getRootView());
                    return false;
                });
                break;
            case R.id.cardBottomRight:
                popup.setOnMenuItemClickListener(item -> {
                    Log.d(TAG, "onClick: Bottom Right: " + item.getTitle());
                    settings.edit((String) item.getTitle(), MagicMirrorUISettings.Position.BOTTOM_RIGHT);
                    populate(v.getRootView());
                    return false;
                });
                break;
        }
    }
}