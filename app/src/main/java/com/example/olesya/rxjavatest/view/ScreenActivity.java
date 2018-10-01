package com.example.olesya.rxjavatest.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.math.MathUtils;
import android.support.v7.widget.LinearLayoutManager;

import com.example.olesya.rxjavatest.Card;
import com.example.olesya.rxjavatest.CardHandler;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.Server;
import com.example.olesya.rxjavatest.adapter.CardPagerAdapter;
import com.example.olesya.rxjavatest.interfaces.ServerCallback;
import com.example.olesya.rxjavatest.ClassModels.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Utils;
import com.example.olesya.rxjavatest.adapter.ListAdapter;
import com.example.olesya.rxjavatest.databinding.ActivityScreenImaginariumBinding;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ScreenActivity extends ServiceHolderActivity implements ServerCallback {

    private ActivityScreenImaginariumBinding mBinding;
    private ListAdapter playerAdapter;
    private CardPagerAdapter cardAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_screen_imaginarium);
        initListView();
        initCardPager();
        startServerService();
        mBinding.buttonSend.setOnClickListener(v -> {
        });
    }

    private void showChoices() {
        for (int i = 0; i < cardAdapter.getItemCount(); i++) {
            CardPagerAdapter.Holder holder = (CardPagerAdapter.Holder) mBinding.cardRv.findViewHolderForAdapterPosition(i);
            holder.addChips(cardAdapter.getVotesByNum(i));
        }
    }

    private void test() {
        cardAdapter.addItem(new Card("0", "player"));
        cardAdapter.addItem(new Card("1", "player0"));
        cardAdapter.addItem(new Card("2", "player1"));
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
        mBinding.cardRv.setHasFixedSize(true);
        mBinding.cardRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.cardRv.setItemAnimator(new SlideInUpAnimator());
        mBinding.cardRv.getItemAnimator().setAddDuration(200);
        cardAdapter = new CardPagerAdapter(new ArrayList<>());
        cardAdapter.setServerCallback(this);
        mBinding.cardRv.setAdapter(cardAdapter);
    }

    protected void startServerService() {
//        test();
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
            mBinding.cardRv.scrollToPosition(cardAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void stopRound() {
        runOnUiThread(() -> {
            showChoices();
            ((Server) mService).countRoundPts(cardAdapter);
            ((Server) mService).showResults(this);
        });
    }

    @Override
    public void onShuffleEnd() {
        runOnUiThread(() -> {
            for (int i = 0; i < cardAdapter.getItemCount(); ++i) {
                CardPagerAdapter.Holder holder = (CardPagerAdapter.Holder) mBinding.cardRv.findViewHolderForAdapterPosition(i);
                if (holder != null)
                    holder.uncoverItem();
            }

            ((Server) getService()).startChoosingStep();
        });
    }

    @Override
    public void onAddUserChoice(String clientName, int currentChoice) {
        cardAdapter.addVote(currentChoice, clientName);
    }

    @Override
    public void onStartNewRound() {
        ((Server) mService).changeLeader();
        ((Server) mService).setTurnNextUser();
    }

    @Override
    public void onUserTurnFinished(Card card) {
        runOnUiThread(() -> {
            cardAdapter.addItem(card);
            mBinding.cardRv.scrollToPosition(cardAdapter.getItemCount() - 1);
            if (cardAdapter.getItemCount() == playerAdapter.getItemCount()) {
                cardAdapter.shuffleCards();
            }
        });
    }
}
