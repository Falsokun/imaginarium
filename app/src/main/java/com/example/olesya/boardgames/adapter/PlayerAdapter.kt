package com.example.olesya.boardgames.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.olesya.boardgames.Entity.Player
import com.example.olesya.boardgames.databinding.ItemPlayerStatusBinding

class PlayerAdapter : RecyclerView.Adapter<PlayerAdapter.Holder>() {

    private var players: MutableList<Player> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemPlayerStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.mBinding.playerName.text = players[position].username
    }

    //TODO:может еще чо надо?
    class Holder(var mBinding: ItemPlayerStatusBinding) : RecyclerView.ViewHolder(mBinding.root)
}