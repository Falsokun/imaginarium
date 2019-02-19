package com.example.olesya.boardgames.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.olesya.boardgames.entity.Card

/**
 * Calculates difference between two data sets and notifies the [CardPagerAdapter] if it was attached
 */
class CardDiffUtil(private val oldList: MutableList<Card>, private val newList: MutableList<Card>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].img == oldList[oldItemPosition].img
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].img == oldList[oldItemPosition].img
                && newList[newItemPosition].isVisible == oldList[oldItemPosition].isVisible
    }
}