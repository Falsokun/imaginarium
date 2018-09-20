package com.example.olesya.rxjavatest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.olesya.rxjavatest.R;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder> {

    private ArrayList<String> mDataSet;

    public ListAdapter(ArrayList<String> dataset) {
        mDataSet = dataset;
    }

    @Override
    public ListAdapter.Holder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list, parent, false);
        ListAdapter.Holder vh = new ListAdapter.Holder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ListAdapter.Holder holder, int position) {
        holder.getText().setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void add(String username) {
        mDataSet.add(username);
        notifyItemRangeInserted(mDataSet.size() - 1, 1);
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
