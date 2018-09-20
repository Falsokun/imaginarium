package com.example.olesya.rxjavatest.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import com.example.olesya.rxjavatest.Client;
import com.example.olesya.rxjavatest.ItemTouchCallback;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Utils;
import com.example.olesya.rxjavatest.adapter.CardPagerAdapter;
import com.example.olesya.rxjavatest.databinding.ActivityCardImaginariumBinding;
import com.example.olesya.rxjavatest.interfaces.ClientCallback;

import java.net.InetAddress;
import java.util.ArrayList;

public class CardActivity extends ServiceHolderActivity  implements ClientCallback {

    private CardPagerAdapter mAdapter;
    private ItemTouchCallback itemTouchCallback;
    private ActivityCardImaginariumBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_imaginarium);
        InetAddress screenAddress = (InetAddress) getIntent()
                .getSerializableExtra(Utils.CLIENT_CONFIG.HOST_CONFIG);
        startClientService(screenAddress);
        mBinding.buttonSend.setOnClickListener(v -> {
            Client client = (Client) getService();
            String message = mBinding.testMsg.getText().toString();
            client.onUserAction(message);
        });
        initCardPager();
    }

    @Override
    public void setCallbacks() {
        ((Client)mService).setCallbacks(this);
    }

    protected void startClientService(InetAddress screenAddress) {
        mServiceIntent = new Intent(this, Client.class);
        mServiceIntent.putExtra(Utils.ACTION_SERVER_SERVICE, false);
        mServiceIntent.putExtra(Utils.CLIENT_CONFIG.HOST_CONFIG, screenAddress);
        mServiceIntent.putExtra(Utils.CLIENT_CONFIG.USERNAME, "player1");
        startService(mServiceIntent);
        bindService(this);
    }

    private void initCardPager() {
        mBinding.cardRv.setHasFixedSize(true);
        mBinding.cardRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new CardPagerAdapter( new ArrayList<>(), serverMessage);
        mBinding.cardRv.setAdapter(mAdapter);
        itemTouchCallback = new ItemTouchCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(itemTouchCallback);
        touchHelper.attachToRecyclerView(mBinding.cardRv);
    }

    @Override
    public void getCardCallback(String card) {
        runOnUiThread(() -> mAdapter.addItem(card));
    }

    @Override
    public void onUserTurnEvent() {
        runOnUiThread(() -> mBinding.title.setText(R.string.user_turn));
        itemTouchCallback.setSwipeEnabled(true);
    }

    @Override
    public void onUserFinishTurnEvent() {
        runOnUiThread(() -> mBinding.title.setText(R.string.finish_turn));
        itemTouchCallback.setSwipeEnabled(false);
    }
}
