package com.example.olesya.boardgames.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.olesya.boardgames.MenuFragment;
import com.example.olesya.boardgames.R;

public class MainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button b = v.findViewById(R.id.g_imaginarium);
        b.setOnClickListener(view -> ((MainActivity)getActivity()).openFragment(new MenuFragment()));
        return v;
    }
}
