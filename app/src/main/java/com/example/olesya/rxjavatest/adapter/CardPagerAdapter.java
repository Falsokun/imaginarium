package com.example.olesya.rxjavatest.adapter;

import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.Utils;

import java.util.ArrayList;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.Holder> {
    private ArrayList<String> mDataSet;
    private MutableLiveData<String> serverMessage;

    public CardPagerAdapter(ArrayList<String> dataset, MutableLiveData<String> message) {
        mDataSet = dataset;
        serverMessage = message;
    }

    @Override
    public CardPagerAdapter.Holder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        Holder vh = new Holder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.getText().setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void onRemove(int adapterPosition) {
        String res = mDataSet.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        serverMessage.postValue(Utils.CLIENT_COMMANDS.SELECTED + "#" + res);
    }

    public void addItem(String card) {
        mDataSet.add(card);
        notifyItemInserted(mDataSet.size() - 1);
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView text;

        public Holder(FrameLayout v) {
            super(v);
            img = v.findViewById(R.id.img_source);
            text = v.findViewById(R.id.card_txt);
        }

        public TextView getText() {
            return text;
        }
    }
}
