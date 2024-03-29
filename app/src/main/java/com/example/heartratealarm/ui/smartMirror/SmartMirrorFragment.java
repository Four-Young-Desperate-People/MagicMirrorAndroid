package com.example.heartratealarm.ui.smartMirror;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.example.heartratealarm.websocket.GenericData;
import com.example.heartratealarm.websocket.WebSocketBase;
import com.google.gson.Gson;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SmartMirrorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SmartMirrorSettings";
    MagicMirrorUISettings settings = new MagicMirrorUISettings();
    private SmartMirrorViewModel smartMirrorViewModel = new SmartMirrorViewModel();
    private Disposable d;
    private WebSocketBase ws;

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

        sync(root);
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
        if (v.getId() == R.id.btnSaveMirror) {
            String json = settings.toJson();
            WebSocketBase ws = new WebSocketBase();
            ws.send(json);
            ws.close();
            return;
        }

        PopupMenu popup = new PopupMenu(getContext(), v);
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

    private void sync(View v) {
        Toast.makeText(v.getContext(), "Syncing with mirror...", Toast.LENGTH_LONG).show();
        Gson gson = new Gson();
        GenericData<Boolean> gd = new GenericData("get_modules_display", false);
        String json = gson.toJson(gd);
        ws = new WebSocketBase();
        d = ws.getMessageObservable().map(s -> {
            if (!s.isPresent()) {
                Log.i(TAG, "Got an empty");
                return false;
            }
            Log.i(TAG, "We got data from the Pi");
            settings.fromJson(s.get());
            Log.d(TAG, "sync: " + s.get());
            return true;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(good -> {
                    if (good) {
                        populate(v.getRootView());
                        ws.close();
                        Log.d(TAG, "sync: done!");
                    }
                }, e -> {
                    Log.e(TAG, "Got an error trying to read data from Pi", e);
                    Toast.makeText(v.getContext(), "Could not find mirror!", Toast.LENGTH_SHORT).show();
                    ws.cancel();
                });

        // Need to set up the listener before we send, otherwise we have a race condition and
        // require to have 2 clicks.
        ws.send(json);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (d != null && !d.isDisposed()) {
            d.dispose();
        }
        ws.close();
    }
}