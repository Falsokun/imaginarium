package com.example.olesya.rxjavatest.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.NumberPicker;

import com.example.olesya.rxjavatest.Card;
import com.example.olesya.rxjavatest.ClassModels.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Client;
import com.example.olesya.rxjavatest.ItemTouchCallback;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.Utils;
import com.example.olesya.rxjavatest.adapter.CardPagerAdapter;
import com.example.olesya.rxjavatest.databinding.ActivityCardImaginariumBinding;
import com.example.olesya.rxjavatest.interfaces.ClientCallback;

import java.net.InetAddress;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

public class CardActivity extends ServiceHolderActivity implements ClientCallback {

    private CardPagerAdapter mAdapter;
    private ItemTouchCallback itemTouchCallback;
    private ActivityCardImaginariumBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String username = null;
        if (getIntent().getExtras() != null)
            username = getIntent().getExtras().getString(Utils.CLIENT_CONFIG.USERNAME);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_card_imaginarium);
        InetAddress screenAddress = (InetAddress) getIntent()
                .getSerializableExtra(Utils.CLIENT_CONFIG.HOST_CONFIG);
        startClientService(screenAddress, username);
        mBinding.buttonSend.setOnClickListener(v -> {
            Client client = (Client) getService();
            String message = mBinding.testMsg.getText().toString();
            client.onUserAction(message);
        });

        initCardPager();
    }

    @Override
    public void setCallbacks() {
        ((Client) mService).setCallbacks(this);
//        ((Client) mService).startHandlingEvents();
    }

    protected void startClientService(InetAddress screenAddress, String username) {
        mServiceIntent = new Intent(this, Client.class);
        mServiceIntent.putExtra(Utils.ACTION_SERVER_SERVICE, false);
        mServiceIntent.putExtra(Utils.CLIENT_CONFIG.HOST_CONFIG, screenAddress);
        mServiceIntent.putExtra(Utils.CLIENT_CONFIG.USERNAME, username);
        startService(mServiceIntent);
        bindService(this);
    }

    private void initCardPager() {
        mBinding.cardRv.setHasFixedSize(true);
        mBinding.cardRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.cardRv.setItemAnimator(new FadeInDownAnimator());
        mBinding.cardRv.getItemAnimator().setAddDuration(100);

        mAdapter = new CardPagerAdapter(new ArrayList<>());
        mAdapter.setClientCallback(this);
        mBinding.cardRv.setAdapter(mAdapter);
        itemTouchCallback = new ItemTouchCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(itemTouchCallback);
        touchHelper.attachToRecyclerView(mBinding.cardRv);
    }

    @Override
    public void addCardCallback(String cardUrl) {
        runOnUiThread(() -> {
            mAdapter.addItem(new Card(cardUrl));
            mBinding.cardRv.scrollToPosition(0);
        });
    }

    @Override
    public void onMainTurnEvent() {
        runOnUiThread(() -> mBinding.title.setText(R.string.choose_topic_and_card));
        itemTouchCallback.setSwipeEnabled(true);
        mAdapter.setMainCaller(true);
    }

    @Override
    public void onMainFinishTurnEvent(String card) {
        runOnUiThread(() -> mBinding.title.setText(R.string.waiting_others));
        serverMessage.postValue(Utils.CLIENT_COMMANDS.CLIENT_MAIN_FINISHED + Utils.DELIM + card);
        itemTouchCallback.setSwipeEnabled(false);
    }

    @Override
    public void onMainStopRoundEvent() {
        runOnUiThread(() ->
                new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle(R.string.finish_round)
                        .setMessage(R.string.finish_round)
                        .setNeutralButton(R.string.OK,
                                (dialog, which) -> serverMessage.postValue(Utils.CLIENT_COMMANDS.CLIENT_MAIN_STOP_FINISHED))
                        .show());
    }

    private void initChooseDialog(int maxNum) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        final NumberPicker np = dialogView.findViewById(R.id.numberPicker);
        np.setMaxValue(maxNum);
        np.setMinValue(1);
        np.setValue(1);
        np.setWrapSelectorWheel(true);
        new AlertDialog.Builder(this)
                .setTitle(R.string.most_suitable_card)
                .setView(dialogView)
                .setCancelable(false)
                .setNeutralButton(R.string.OK, (dialog, which) ->
                        serverMessage.postValue(Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE_FINISHED
                                + Utils.DELIM
                                + np.getValue())).show();
    }

    @Override
    public void onUserChooseEvent(int playersNum) {
        runOnUiThread(() -> initChooseDialog(playersNum));
    }

    @Override
    public void onUserTurnEvent() {
        runOnUiThread(() -> mBinding.title.setText(R.string.user_turn));
        itemTouchCallback.setSwipeEnabled(true);
        mAdapter.setMainCaller(false);
    }

    @Override
    public void onUserFinishTurnEvent(String card) {
        serverMessage.postValue(Utils.CLIENT_COMMANDS.CLIENT_USER_FINISHED + Utils.DELIM + card);
        runOnUiThread(() -> {
            mBinding.title.setText(R.string.finish_turn);
            itemTouchCallback.setSwipeEnabled(false);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getService().onDestroy();
    }
}
