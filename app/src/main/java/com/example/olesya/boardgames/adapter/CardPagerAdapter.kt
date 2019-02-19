package com.example.olesya.boardgames.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.olesya.boardgames.databinding.LayoutCardBinding
import com.example.olesya.boardgames.entity.Card

class CardPagerAdapter : RecyclerView.Adapter<CardPagerAdapter.Holder>() {

    var dataset: MutableList<Card> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //TODO: ??
        holder.mBinding.card.setFaceUp(dataset[position].img)
//        holder.mBinding.votesContainer.removeAllViews()
//        dataset[position].isVisible.subscribe { visibility -> holder.mBinding.card.animateFaceUpChange(visibility) }
    }

    fun <T : Card> setData(cards: MutableList<T>) {
        dataset.clear()
        dataset.addAll(cards)
    }

    fun add(card: Card) {
        dataset.add(0, card)
        notifyItemInserted(0)
    }

    fun remove(item: Int) {
        dataset.removeAt(item)
        notifyItemRemoved(item)
    }

    inner class Holder(var mBinding: LayoutCardBinding) : RecyclerView.ViewHolder(mBinding.root),
            View.OnLongClickListener {

        init {
//            mBinding.root.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
//            dataset[adapterPosition].isVisible.onNext(!dataset[adapterPosition].isVisible.value!!)
            return false
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