package com.example.olesya.boardgames.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.olesya.boardgames.Card;
import com.example.olesya.boardgames.R;
import com.example.olesya.boardgames.databinding.LayoutCardBinding;
import com.example.olesya.boardgames.interfaces.ClientCallback;
import com.example.olesya.boardgames.interfaces.ScreenCallback;

import java.util.ArrayList;
import java.util.Random;

public class CardPagerAdapter extends RecyclerView.Adapter<CardPagerAdapter.Holder> {
    private ArrayList<Card> mDataSet;
    private ArrayList<ArrayList<String>> votesData = new ArrayList<>();
    private ClientCallback clientCallback;
    private ScreenCallback itemCallback;
    private boolean isMainCaller;
    private boolean isUser = true;

    public CardPagerAdapter(ArrayList<Card> dataset, boolean isUser) {
        mDataSet = dataset;
        this.isUser = isUser;
        for (int i = 0; i < mDataSet.size(); i++) {
            votesData.add(new ArrayList<>());
        }
    }

    @Override
    public CardPagerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
//        holder.getText().setText(mDataSet.get(position).getImg());
        ArrayList<String> votes = votesData.get(position);
        Glide.with(holder.mBinding.getRoot().getContext())
                .load(mDataSet.get(position).getImg())
                .apply(new RequestOptions().centerCrop().transform(new RoundedCorners(10)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mBinding.imgSource);
        holder.mBinding.votesContainer.removeAllViews();
        if (isUser) {
            holder.uncoverItem();
        }
        holder.addChips(votes);
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

    public void insert(int position, Card card) {
        mDataSet.add(position, card);
        votesData.add(new ArrayList<>());
        notifyItemInserted(0);
    }

    public void add(Card card) {
        mDataSet.add(card);
        votesData.add(new ArrayList<>());
        notifyItemInserted(mDataSet.size() - 1);
    }

    public void addVote(int cardNum, String playerName) {
        if (votesData.size() < cardNum)
            return;

        votesData.get(cardNum).add(playerName);
    }

    public void setMainCaller(boolean mainCaller) {
        this.isMainCaller = mainCaller;
    }

    public void shuffleCards() {
        ArrayList<Card> set = new ArrayList<>();
        Random r = new Random();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (set == mDataSet) {
                    itemCallback.onShuffleEnd();
                    return;
                }

                int position = r.nextInt(mDataSet.size());
                set.add(mDataSet.remove(position));
                notifyItemRangeRemoved(position, 1);

                if (mDataSet.size() <= 0) {
                    mDataSet = set;
                    notifyItemRangeInserted(1, mDataSet.size());
                }

                handler.postDelayed(this, 500);
            }
        }, 0);
    }

    public ArrayList<String> getVotesByNum(int currentChoice) {
        return votesData.get(currentChoice);
    }

    public ArrayList<ArrayList<String>> getVotes() {
        return votesData;
    }

    public int findCardIndex(String cardUrl) {
        for (int i = 0; i < mDataSet.size(); i++) {
            if (mDataSet.get(i).getImg().equals(cardUrl))
                return i;
        }

        return -1;
    }

    public ArrayList<Card> getData() {
        return mDataSet;
    }

    public void clearData() {
        mDataSet.clear();
    }

    public static class Holder extends RecyclerView.ViewHolder implements View.OnLongClickListener, Animator.AnimatorListener {
        //        public ImageView img;
        public LayoutCardBinding mBinding;
        private boolean isCardFace = true;

        public Holder(LayoutCardBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnLongClickListener(this);
        }

        public TextView getText() {
            return mBinding.cardTxt;
        }

        @Override
        public boolean onLongClick(View v) {
            if (isCardFace) {
                startAnimation(mBinding.cardFace, mBinding.imgSource);
            } else {
                startAnimation(mBinding.imgSource, mBinding.cardFace);
            }

            return false;
        }

        public void startAnimation(View hide, View show) {
            AnimatorSet animationhide = (AnimatorSet) AnimatorInflater.loadAnimator(mBinding.getRoot().getContext(),
                    R.animator.animation_rotate_hide);
            animationhide.setTarget(hide);
            animationhide.addListener(this);
            AnimatorSet animationshow = (AnimatorSet) AnimatorInflater.loadAnimator(mBinding.getRoot().getContext(),
                    R.animator.animation_rotate_reveal);
            animationshow.setTarget(show);
            animationhide.start();
            animationshow.start();
            float scale = hide.getContext().getResources().getDisplayMetrics().density;
            int distance = 8000;
            hide.setCameraDistance(distance * scale);
            show.setCameraDistance(distance * scale);
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            isCardFace = !isCardFace;
            mBinding.getRoot().setLongClickable(true);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        public void uncoverItem() {
            if (isCardFace) {
                startAnimation(mBinding.cardFace, mBinding.imgSource);
            } else {
                startAnimation(mBinding.imgSource, mBinding.cardFace);
            }
        }

        public void addChips(ArrayList<String> votes) {
            for (String vote : votes) {
                addChip(vote);
            }
        }

        public void addChip(String vote) {
            Context context = mBinding.votesContainer.getContext();
            Button bt = new Button(context);
            bt.setText(vote);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            int dp16 = dpToPx(context, 16);
            params.setMargins(dp16, dp16, dp16, dp16);
            bt.setLayoutParams(params);
            bt.setWidth(dpToPx(context, 50));
            bt.setHeight(dpToPx(context, 50));
            bt.setBackground(getRadialGradient(Color.parseColor("#000000"),
                    Color.parseColor("#ffffff")));
            mBinding.votesContainer.addView(bt);
        }

        private int dpToPx(Context context, int dp) {
            Resources r = context.getResources();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );

            return (int) px;
        }

        public GradientDrawable getRadialGradient(int colorStart, int colorEnd) {
            int[] colors = {colorStart, colorEnd};

            //create a new gradient color
            GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors); //#97712F this is the end color of gradient
            g.setGradientType(GradientDrawable.RADIAL_GRADIENT); // making it circular gradient
            g.setGradientRadius(300);  // radius of the circle
            g.setCornerRadius(50);
            return g;
        }
    }

    public void setClientCallback(ClientCallback callback) {
        clientCallback = callback;
    }

    public void setItemCallback(ScreenCallback callback) {
        itemCallback = callback;
    }
}
