package com.example.olesya.boardgames.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.olesya.boardgames.R;

import java.util.ArrayList;

public class PlayerStatusAdapter extends RecyclerView.Adapter<PlayerStatusAdapter.Holder> {

    private ArrayList<String> mDataSet;

    public PlayerStatusAdapter(ArrayList<String> dataset) {
        mDataSet = dataset;
    }

    @Override
    public PlayerStatusAdapter.Holder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list, parent, false);
        PlayerStatusAdapter.Holder vh = new PlayerStatusAdapter.Holder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PlayerStatusAdapter.Holder holder, int position) {
        holder.getText().setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void add(String username) {
        mDataSet.add(username);
        notifyItemRangeChanged(mDataSet.size() - 1, 1);
    }

    public void removePlayer(String name) {
        int index = findIndex(name);
        if (index != -1) {
            mDataSet.remove(name);
            notifyItemRemoved(index);
        }
    }

    private int findIndex(String name) {
        for (int i = 0; i < mDataSet.size(); i++) {
            if (mDataSet.get(i).equals(name))
                return i;
        }

        return -1;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView text;

        public Holder(FrameLayout v) {
            super(v);
            text = v.findViewById(R.id.pl_name_tv);
        }

        public TextView getText() {
            return text;
        }
    }
}
