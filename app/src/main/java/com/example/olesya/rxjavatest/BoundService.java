package com.example.olesya.rxjavatest;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

//TODO: может это сделать не интерфейсом а классом, который наследует сервис и посмотреть чо общего в общении клиента и сервера
//TODO: потому что там они общаются одинаково плюс минус
public class BoundService extends Service {

    protected final int PORT_NUMBER = 8888;
    protected MutableLiveData<String> message = new MutableLiveData<>();
    protected MyBinder binder = new BoundService.MyBinder();

    public void setMessage(MutableLiveData<String> message) {
        this.message = message;
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
