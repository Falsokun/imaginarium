package com.example.olesya.boardgames

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class NonNullMutableLiveData<T>(private val defaultValue: T) : MutableLiveData<T>() {
    override fun getValue(): T = super.getValue() ?: defaultValue

    fun observe(owner: LifecycleOwner, body: (T) -> Unit) {
        observe(owner, Observer<T> {
            body(it ?: defaultValue)
        })
    }
}