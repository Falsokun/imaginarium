package com.example.olesya.boardgames;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.v7.widget.RecyclerView;

import com.example.olesya.boardgames.Entity.Card;
import com.example.olesya.boardgames.adapter.CardPagerAdapter;
import com.example.olesya.boardgames.adapter.PlayerStatusAdapter;
import com.example.olesya.boardgames.interfaces.ItemCallback;

import java.util.ArrayList;

public class ScreenViewModel extends ViewModel implements ItemCallback {

    /**
     * Adapter which handles player status & results;
     */
    private PlayerStatusAdapter playerAdapter;

    /**
     * Adapter which handles cards on the desk
     */
    private CardPagerAdapter cardAdapter;

    private MutableLiveData<String> message = new MutableLiveData<>();

    public ScreenViewModel() {
        playerAdapter = new PlayerStatusAdapter(new ArrayList<>());
        cardAdapter = new CardPagerAdapter(new ArrayList<>(), false);
    }

    public void showChoices(RecyclerView recyclerView) {
        for (int i = 0; i < cardAdapter.getItemCount(); i++) {
            CardPagerAdapter.Holder holder = (CardPagerAdapter.Holder) recyclerView.findViewHolderForAdapterPosition(i);
            holder.addChips(cardAdapter.getVotesByNum(i));
        }
    }

    public PlayerStatusAdapter getPlayerAdapter() {
        return playerAdapter;
    }

    public CardPagerAdapter getCardAdapter() {
        return cardAdapter;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    //region callbacks
    @Override
    public void onSelectedCardEvent(Card card) {
        cardAdapter.insert(0, card);
    }

    @Override
    public void onAddUserChoice(String clientName, int currentChoice) {
        cardAdapter.addVote(currentChoice, clientName);
    }

    @Override
    public void onUserTurnFinished(Card card) {
        cardAdapter.add(card);
        if (cardAdapter.getItemCount() == playerAdapter.getItemCount()) {
            cardAdapter.shuffleCards();
        }
    }

    public void unCoverItems(RecyclerView cardRv) {
        for (int i = 0; i < cardAdapter.getItemCount(); ++i) {
            CardPagerAdapter.Holder holder = (CardPagerAdapter.Holder) cardRv.findViewHolderForAdapterPosition(i);
            if (holder != null)
                holder.uncoverItem();
        }
    }

    public void startNewRound(RecyclerView cardRv) {
        cardRv.removeAllViews();
        cardAdapter.getVotes().clear();
        cardAdapter.clearData();
    }
    //endregion
}
