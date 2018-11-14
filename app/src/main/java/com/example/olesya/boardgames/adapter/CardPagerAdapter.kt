package com.example.olesya.boardgames.adapter

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.olesya.boardgames.Entity.ImaginariumCard
import com.example.olesya.boardgames.R
import com.example.olesya.boardgames.databinding.LayoutCardBinding


class CardPagerAdapter : RecyclerView.Adapter<CardPagerAdapter.Holder>() {

    var dataset: MutableList<ImaginariumCard> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_image_black_24dp)
                .centerCrop()
                .transform(RoundedCorners(10))

        Glide.with(holder.mBinding.root.context)
                .load(dataset[position].img)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mBinding.imgSource)

        //TODO: ??
        holder.mBinding.votesContainer.removeAllViews()
//        holder.showItem(dataset[position].isVisible.value)
        dataset[position].isVisible.subscribe { visibility -> holder.uncoverItem(visibility) }
        /*ArrayList<String> votes = votesData.get(position);
        holder.mBinding.votesContainer.removeAllViews();
        if (isUser) {
            holder.uncoverItem();
        }
        holder.addChips(votes);*/
    }

    fun setData(cards: List<ImaginariumCard>) {
        dataset.clear()
        dataset.addAll(cards)
        notifyItemRangeInserted(0, dataset.size)
    }

    inner class Holder(var mBinding: LayoutCardBinding) : RecyclerView.ViewHolder(mBinding.root),
            View.OnLongClickListener {

        init {
            mBinding.root.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            //TODO: !какой-то монстр с восклицательными знаками с обеих сторон!!
            dataset[adapterPosition].isVisible.onNext(!dataset[adapterPosition].isVisible.value!!)
            return false
        }

        fun uncoverItem(isVisible: Boolean) {
            if (isVisible) {
                startAnimation(mBinding.cardFace, mBinding.imgSource)
            } else {
                startAnimation(mBinding.imgSource, mBinding.cardFace)
            }
        }

        fun startAnimation(hide: View, show: View) {
            val animationHide = AnimatorInflater.loadAnimator(mBinding.root.context,
                    R.animator.animation_rotate_hide) as AnimatorSet
            animationHide.setTarget(hide)
//            animationHide.addListener(this)
            val animationShow = AnimatorInflater.loadAnimator(mBinding.root.context,
                    R.animator.animation_rotate_reveal) as AnimatorSet
            animationShow.setTarget(show)
            animationHide.start()
            animationShow.start()
            val scale = hide.context.resources.displayMetrics.density
            val distance = 8000
            hide.cameraDistance = distance * scale
            show.cameraDistance = distance * scale
        }

        fun showItem(isVisible: Boolean) {
            if (isVisible) {
                mBinding.cardFace.visibility = View.GONE
                mBinding.imgSource.visibility = View.VISIBLE
            } else {
                mBinding.cardFace.visibility = View.VISIBLE
                mBinding.imgSource.visibility = View.GONE
            }
        }
    }

    //TODO: вывод очков и кто за кого проголосовал
//    public void addChips(ArrayList<String> votes) {
//        for (String vote : votes) {
//            addChip(vote);
//        }
//    }
//
//    public void addChip(String vote) {
//        Context context = mBinding.votesContainer.getContext();
//        Button bt = new Button(context);
//        bt.setText(vote);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
//                FrameLayout.LayoutParams.WRAP_CONTENT);
//        int dp16 = dpToPx(context, 16);
//        params.setMargins(dp16, dp16, dp16, dp16);
//        bt.setLayoutParams(params);
//        bt.setWidth(dpToPx(context, 50));
//        bt.setHeight(dpToPx(context, 50));
//        bt.setBackground(getRadialGradient(Color.parseColor("#000000"),
//                Color.parseColor("#ffffff")));
//        mBinding.votesContainer.addView(bt);
//    }
//
//    private int dpToPx(Context context, int dp) {
//        Resources r = context.getResources();
//        float px = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//        dp,
//        r.getDisplayMetrics()
//        );
//
//        return (int) px;
//    }
//
//    public GradientDrawable getRadialGradient(int colorStart, int colorEnd) {
//        int[] colors = {colorStart, colorEnd};
//
//        //create a new gradient color
//        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors); //#97712F this is the end color of gradient
//        g.setGradientType(GradientDrawable.RADIAL_GRADIENT); // making it circular gradient
//        g.setGradientRadius(300);  // radius of the circle
//        g.setCornerRadius(50);
//        return g;
//    }
//
}