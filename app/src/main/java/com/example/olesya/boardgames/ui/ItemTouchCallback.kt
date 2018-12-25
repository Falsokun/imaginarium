package com.example.olesya.boardgames.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.example.olesya.boardgames.adapter.CardPagerAdapter

class ItemTouchCallback(val adapter: CardPagerAdapter): ItemTouchHelper.Callback() {

    lateinit var runnable: (position: Int) -> Unit

    var isSwipeEnabled: Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean {
        return isSwipeEnabled
    }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        val dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
        val swipeFlags = ItemTouchHelper.UP
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        val position = viewHolder?.adapterPosition ?: return
        runnable.invoke(position)
    }
}