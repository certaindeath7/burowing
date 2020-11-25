package com.example.burowing2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.burowing2.R;

public class FragmentDependingMessage extends Fragment {
    private static final String TAG = "Depending Message";
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_depending_message,container,false);
        textView = (TextView) view.findViewById(R.id.tab_depending);
        textView.setText("Depending Message");
        return view;

    }
}