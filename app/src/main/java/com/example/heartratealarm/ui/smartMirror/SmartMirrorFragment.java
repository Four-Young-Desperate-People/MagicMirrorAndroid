package com.example.heartratealarm.ui.smartMirror;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartratealarm.R;

public class SmartMirrorFragment extends Fragment {

    private SmartMirrorViewModel smartMirrorViewModel;
    String[] modules = {"Compliments", "Clock", "Current Weather", "Weather Forecast", "News Feed"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        smartMirrorViewModel =
                new ViewModelProvider(this).get(SmartMirrorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_smart_mirror, container, false);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modules);

        Spinner spinner = root.findViewById(R.id.spinner2);
        spinner.setAdapter(arrayAdapter);
        return root;
    }
}