package com.example.olesya.boardgames.ui

import android.arch.lifecycle.MutableLiveData
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemTouchCallback : ItemTouchHelper.Callback() {

    var pickedData: MutableLiveData<Int> = MutableLiveData()

    var isSwipeEnabled = false

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.START or ItemTouchHelper.END,
                if (isSwipeEnabled) ItemTouchHelper.UP else 0)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        val position = viewHolder?.adapterPosition ?: return
        pickedData.postValue(position)
    }
}