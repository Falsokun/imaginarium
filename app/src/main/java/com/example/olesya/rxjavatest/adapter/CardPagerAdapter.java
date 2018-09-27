package com.example.olesya.rxjavatest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.olesya.rxjavatest.Card;
import com.example.olesya.rxjavatest.R;
import com.example.olesya.rxjavatest.interfaces.ClientCallback;

import java.util.ArrayList;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.Holder> {
    private ArrayList<Card> mDataSet;
    private ClientCallback clientCallback;
    private boolean isMainCaller;

    public CardPagerAdapter(ArrayList<Card> dataset) {
        mDataSet = dataset;
    }

    @Override
    public CardPagerAdapter.Holder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.getText().setText(mDataSet.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void onRemove(int adapterPosition) {
        Card res = mDataSet.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        if (clientCallback != null) {
            if (isMainCaller) {
                clientCallback.onMainFinishTurnEvent(res.getImg());
            } else {
                clientCallback.onUserFinishTurnEvent(res.getImg());
            }
        }
    }

    public void addItem(Card card) {
        mDataSet.add(card);
        notifyItemInserted(0);
    }

    public void setMainCaller(boolean mainCaller) {
        this.isMainCaller = mainCaller;
    }

    public static class Holder extends RecyclerView.ViewHolder {
//        public ImageView img;
        public TextView text;

        public Holder(FrameLayout v) {
            super(v);
//            img = v.findViewById(R.id.img_source);
            text = v.findViewById(R.id.card_txt);
        }

        public TextView getText() {
            return text;
        }
    }

    public void setClientCallback(ClientCallback callback) {
        clientCallback = callback;
    }
}
