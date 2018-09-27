package com.example.olesya.rxjavatest.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.olesya.rxjavatest.Card;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.Server;
import com.example.olesya.rxjavatest.adapter.CardPagerAdapter;
import com.example.olesya.rxjavatest.interfaces.ServerCallback;
import com.example.olesya.rxjavatest.ClassModels.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Utils;
import com.example.olesya.rxjavatest.adapter.ListAdapter;
import com.example.olesya.rxjavatest.databinding.ActivityScreenImaginariumBinding;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

public class ScreenActivity extends ServiceHolderActivity implements ServerCallback {

    private ActivityScreenImaginariumBinding mBinding;
    private ListAdapter playerAdapter;
    private CardPagerAdapter cardAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_imaginarium);
        initListView();
        initCardPager();
        startServerService();
        mBinding.buttonSend.setOnClickListener(v -> {
            Server server = (Server) getService();
            String message = mBinding.testMsg.getText().toString();
            server.onUserAction(message);
        });
    }

    @Override
    public void setCallbacks() {
        ((Server) mService).setCallbacks(this);
    }

    private void initListView() {
        //init player rv
        ArrayList<String> str = new ArrayList<>();
        playerAdapter = new ListAdapter(str);
        mBinding.playersStatusRv.setHasFixedSize(false);
        mBinding.playersStatusRv.setAdapter(playerAdapter);
        mBinding.playersStatusRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initCardPager() {
        recyclerView = findViewById(R.id.card_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new FlipInTopXAnimator());
        recyclerView.getItemAnimator().setAddDuration(500);
        cardAdapter = new CardPagerAdapter(new ArrayList<>());
        recyclerView.setAdapter(cardAdapter);
    }

    protected void startServerService() {
        mServiceIntent = new Intent(this, Server.class);
        mServiceIntent.putExtra(Utils.CLIENT_NUM, 5);
        int playerNum = getIntent().getExtras().getInt(Utils.CLIENT_NUM);
        int win = getIntent().getExtras().getInt(Utils.WIN_PTS);
        mServiceIntent.putExtra(Utils.CLIENT_NUM, playerNum);
        mServiceIntent.putExtra(Utils.WIN_PTS, win);
        startService(mServiceIntent);
    }

    @Override
    public void onAddUserEvent(String username) {
        runOnUiThread(() -> playerAdapter.add(username));
    }

    @Override
    public void onSelectedCardEvent(Card card) {
        runOnUiThread(() -> {
            cardAdapter.addItem(card);
            recyclerView.scrollToPosition(cardAdapter.getItemCount() - 1);
    });
}

    @Override
    public void uncoverCardsAnimation() {
        Utils.showAlert(this, "uncover");
    }

    @Override
    public void onUserTurnFinished(Card card) {
        runOnUiThread(() -> {
            cardAdapter.addItem(card);
            recyclerView.scrollToPosition(cardAdapter.getItemCount() - 1);
            if (cardAdapter.getItemCount() == playerAdapter.getItemCount()) {
                ((Server) mService).startChoosingStep();
            }
        });
    }
}
