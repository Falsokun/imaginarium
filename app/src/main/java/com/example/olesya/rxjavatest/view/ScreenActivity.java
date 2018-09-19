package com.example.olesya.rxjavatest.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.olesya.rxjavatest.Client;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.Server;
import com.example.olesya.rxjavatest.ServiceHolderActivity;
import com.example.olesya.rxjavatest.Utils;

public class ScreenActivity extends ServiceHolderActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_imaginarium);
        startServerService();

        findViewById(R.id.button_send).setOnClickListener(v -> {
            Server server = (Server) getService();
            String message = ((TextView) findViewById(R.id.test_msg)).getText().toString();
            server.onUserAction(message);
        });
    }

    protected void startServerService() {
        mServiceIntent = new Intent(this, Server.class);
        mServiceIntent.putExtra(Utils.ACTION_SERVER_SERVICE, true);
        startService(mServiceIntent);
    }
}
