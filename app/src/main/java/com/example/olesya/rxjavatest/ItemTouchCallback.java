package com.example.olesya.rxjavatest;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.olesya.rxjavatest.adapter.CardPagerAdapter;

public class ItemTouchCallback extends ItemTouchHelper.Callback {

    private CardPagerAdapter mAdapter;

    public ItemTouchCallback(CardPagerAdapter adapter) {
        mAdapter = adapter;
    }

    private boolean isSwipeEnabled = false;

    @Override
    public boolean isItemViewSwipeEnabled() {
        return isSwipeEnabled;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int swipeFlags = ItemTouchHelper.UP;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onRemove(viewHolder.getAdapterPosition());
    }

    public void setSwipeEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }
}