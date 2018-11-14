package com.example.olesya.boardgames.connection;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class BoundService extends Service {

    protected final int PORT_NUMBER = 8888;

    protected MutableLiveData<String> serviceMessage = new MutableLiveData<>();

    protected MyBinder binder = new BoundService.MyBinder();

    public void setMessage(MutableLiveData<String> message) {
        this.serviceMessage = message;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBinder extends Binder {
        BoundService getService() {
            return BoundService.this;
        }
    }
}
