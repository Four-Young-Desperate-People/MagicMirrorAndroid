package com.example.heartratealarm.ui.smartMirror;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.R;

public class SmartMirrorFragment extends Fragment {

    private SmartMirrorViewModel smartMirrorViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        smartMirrorViewModel =
                new ViewModelProvider(this).get(SmartMirrorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_smart_mirror, container, false);
        return root;
    }
}