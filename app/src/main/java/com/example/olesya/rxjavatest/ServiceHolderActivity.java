package com.example.olesya.rxjavatest;

import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class ServiceHolderActivity extends AppCompatActivity {

    private BoundService mService;
    protected Intent mServiceIntent;

    private ServiceConnection serviceConn;
    private boolean serviceBound = false;
    public MutableLiveData<String> message = new MutableLiveData<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initServiceConnection();
        message.observe(this, v -> Utils.showAlert(this, message.getValue()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(this);
   }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void initServiceConnection() {
        serviceConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mService = ((BoundService.MyBinder) binder).getService();
                mService.setMessage(message);
                serviceBound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                serviceBound = false;
            }
        };
    }

    public void bindService(Context context) {
        if (mServiceIntent != null)
            context.bindService(mServiceIntent, serviceConn, BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        if (!serviceBound || mServiceIntent == null) return;
        context.unbindService(serviceConn);
        serviceBound = false;
    }

    public BoundService getService() {
        return mService;
    }
}
