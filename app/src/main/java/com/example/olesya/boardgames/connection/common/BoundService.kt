package com.example.olesya.boardgames.connection.common

import android.app.Service
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Binder
import android.os.IBinder

open class BoundService: Service() {

    val PORT_NUMBER = 8888

    var screenMessage: MutableLiveData<String> = MutableLiveData()

    val binder: MyBinder = MyBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class MyBinder: Binder() {
        fun getService(): BoundService {
            return this@BoundService
        }
    }

}