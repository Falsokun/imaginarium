package com.example.olesya.boardgames.view;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.olesya.boardgames.Card;
import com.example.olesya.boardgames.models.ServiceHolderActivity;
import com.example.olesya.boardgames.R;
import com.example.olesya.boardgames.ScreenViewModel;
import com.example.olesya.boardgames.Server;
import com.example.olesya.boardgames.Utils;
import com.example.olesya.boardgames.databinding.ActivityScreenImaginariumBinding;
import com.example.olesya.boardgames.interfaces.ScreenCallback;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ScreenActivity extends ServiceHolderActivity implements ScreenCallback {

    private ActivityScreenImaginariumBinding mBinding;
    private ScreenViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_imaginarium);
        viewModel = ViewModelProviders.of(this).get(ScreenViewModel.class);
        initListView();
        initCardPager();
        startServerService();
        viewModel.getMessage().observe(this, s -> Utils.showAlert(ScreenActivity.this, s));
        mBinding.buttonSend.setOnClickListener(v -> serviceMessage.postValue("anything"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mServiceIntent);
    }

    private void test() {
        new Thread(() -> {
            viewModel.onSelectedCardEvent(((Server) mService).getRandomCard());
            viewModel.onSelectedCardEvent(((Server) mService).getRandomCard());
            viewModel.onSelectedCardEvent(((Server) mService).getRandomCard());
            viewModel.onSelectedCardEvent(((Server) mService).getRandomCard());
        }).start();
    }

    @Override
    public void setCallbacks() {
        ((Server) mService).setCallbacks(viewModel);
        ((Server) mService).setScreenCallbacks(this);
//        test();
    }

    private void initListView() {
        //init player rv
        mBinding.playersStatusRv.setHasFixedSize(false);
        mBinding.playersStatusRv.setAdapter(viewModel.getPlayerAdapter());
        mBinding.playersStatusRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initCardPager() {
        mBinding.cardRv.setHasFixedSize(true);
        mBinding.cardRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.cardRv.setItemAnimator(new SlideInUpAnimator());
        mBinding.cardRv.getItemAnimator().setAddDuration(200);
        mBinding.cardRv.setAdapter(viewModel.getCardAdapter());
        viewModel.getCardAdapter().setItemCallback(this);
        viewModel.getCardAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                runOnUiThread(() -> mBinding.cardRv.scrollToPosition(positionStart));
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    protected void startServerService() {
        mServiceIntent = new Intent(this, Server.class);
        if (getIntent().getExtras() != null) {
            int playerNum = getIntent().getExtras().getInt(Utils.CLIENT_NUM);
            int win = getIntent().getExtras().getInt(Utils.WIN_PTS);
            mServiceIntent.putExtra(Utils.CLIENT_NUM, playerNum);
            mServiceIntent.putExtra(Utils.WIN_PTS, win);
        }

        startService(mServiceIntent);
    }

    private void showRoundResults() {
        if (((Server) mService).hasClientWinPts()) {
            Utils.showAlert(this, getResources().getString(R.string.end_of_game));
            ((Server) mService).stopGame();
            return;
        }

        String results = ((Server) mService).getStringResults();
        new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle(R.string.round_results)
                .setMessage(results)
                .setCancelable(false)
                .setNeutralButton(R.string.OK,
                        (dialog, which) -> onStartNewRound())
                .show();
    }

    @Override
    public void onStartNewRound() {
        ((Server) mService).changeLeader();
        viewModel.startNewRound(mBinding.cardRv);

        new Thread(() -> ((Server) mService).setTurnNextUser()).start();
    }

    @Override
    public void stopRound() {
        ((Server) mService).sendCardsToUsers();
        runOnUiThread(() -> {
            viewModel.showChoices(mBinding.cardRv);
            ((Server) mService).countRoundPts(viewModel.getCardAdapter());
            showRoundResults();
        });
    }

    @Override
    public void onAddUserEvent(String clientName) {
        runOnUiThread(() -> viewModel.getPlayerAdapter().add(clientName));
    }

    @Override
    public void onRemoveUserEvent(String clientName) {
        serviceMessage.postValue(clientName + " out");
        runOnUiThread(() -> viewModel.getPlayerAdapter().removePlayer(clientName));
        ((Server) mService).removePlayer(clientName);
    }

    @Override
    public void initDesk(String username) {
        for (int i = 0; i < Utils.DEFAULT_CARDS_NUM; i++) {
            ((Server) mService).sendRandomCardToUser(username);
        }
    }

    @Override
    public void onShuffleEnd() {
        runOnUiThread(() -> {
            viewModel.unCoverItems(mBinding.cardRv);
            ((Server) getService()).startChoosingStep();
        });
    }
}
