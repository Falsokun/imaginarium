package com.example.olesya.rxjavatest.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.olesya.rxjavatest.Client;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Utils;

import java.net.InetAddress;

public class CardActivity extends ServiceHolderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_imaginarium);
        InetAddress screenAddress = (InetAddress) getIntent()
                .getSerializableExtra(Utils.CLIENT_COMMANDS.HOST_CONFIG);
        startClientService(screenAddress);
        findViewById(R.id.button_send).setOnClickListener(v -> {
            Client client = (Client) getService();
            String message = ((TextView) findViewById(R.id.test_msg)).getText().toString();
            client.onUserAction(message);
        });
    }

    protected void startClientService(InetAddress screenAddress) {
        mServiceIntent = new Intent(this, Client.class);
        mServiceIntent.putExtra(Utils.ACTION_SERVER_SERVICE, false);
        mServiceIntent.putExtra(Utils.CLIENT_COMMANDS.HOST_CONFIG, screenAddress);
        startService(mServiceIntent);
        bindService(this);
    }

}
