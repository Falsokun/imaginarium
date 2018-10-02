package com.example.olesya.boardgames;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.olesya.boardgames.databinding.FragmentMenuBinding;

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
        mBinding.search.setOnClickListener(mvmodel.getOnSearchClickListener(getActivity(), this));
        mBinding.start.setOnClickListener(mvmodel.getOnStartClickListener(mBinding.switcher,
                mBinding.playerName, mBinding.playerNum, mBinding.ptsNum));
        mBinding.switcher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mBinding.statusMsg.setText("Server, waiting for connections");
                mBinding.search.setVisibility(View.GONE);
                mBinding.playerName.setVisibility(View.GONE);
                mBinding.screenContainer.setVisibility(View.VISIBLE);
                mvmodel.discoverPeers();
            } else {
                mBinding.screenContainer.setVisibility(View.GONE);
                mBinding.search.setVisibility(View.VISIBLE);
                mBinding.playerName.setVisibility(View.VISIBLE);
                mBinding.statusMsg.setText(R.string.no_one_connected_to);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == Utils.PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mvmodel.requestPeers();
        }
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
