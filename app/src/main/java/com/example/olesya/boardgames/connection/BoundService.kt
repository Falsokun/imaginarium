package com.example.olesya.boardgames.connection

import android.app.Service
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Binder
import android.os.IBinder

open class BoundService: Service() {

    val PORT_NUMBER = 8888

    var serviceMessage: MutableLiveData<String> = MutableLiveData()

    val binder: BoundService.MyBinder = MyBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class MyBinder: Binder() {
        fun getService(): BoundService {
            return this@BoundService
        }
    }

}