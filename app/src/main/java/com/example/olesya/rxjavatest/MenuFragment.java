package com.example.olesya.rxjavatest;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olesya.rxjavatest.databinding.FragmentMenuBinding;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding mBinding;
    private MenuViewModel mvmodel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        mvmodel = new MenuViewModel(this);
        initListeners();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return mBinding.getRoot();
    }

    private void initListeners() {
        mBinding.search.setOnClickListener(mvmodel.getOnSearchClickListener(getActivity()));
        mBinding.start.setOnClickListener(mvmodel.getOnStartClickListener(mBinding.switcher));
    }

    @Override
    public void onResume() {
        super.onResume();
        mvmodel.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mvmodel.onPause();
    }
}
