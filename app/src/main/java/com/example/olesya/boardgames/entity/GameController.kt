package com.example.olesya.boardgames.entity

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.connection.server.ServerCallback

/**
 * Players with their cards and scores
 */
abstract class GameController constructor(context: Context,
                                      var players: MutableList<Player> = mutableListOf(),
                                      val winPts: Int,
                                      val sender: ServerCallback
) {

    var deck: Deck = Deck(context)

    var screenCards: MutableLiveData<MutableList<ImaginariumCard>> = MutableLiveData()

    var screenMessage: MutableLiveData<String> = MutableLiveData()

    var round: Int = 1

    /**
     * ????
     */
    var step: MutableLiveData<String> = MutableLiveData()

    var leaderPosition: Int = -1

    lateinit var lcOwner: LifecycleOwner

    init {
        screenCards.value = ArrayList()
        //TODO:!!!
        step.value = "init"
    }

    abstract fun clientPicksCard(username: String, card: String)

    fun addCard(card: ImaginariumCard) {
        val cur = screenCards.value
        //won't be null if null arraylist won't be explicitly set to screencards
        cur?.add(0, card)
        //post(?); post is safe; //TODO: check later
        screenCards.postValue(cur)
    }

    fun shuffleCards() {
        //TODO:
    }

    fun isRoundOver(): Boolean {
        return players.size == screenCards.value?.size
    }

    fun onAddUserEvent(username: String, renamedFrom: String): Player {
        val player = players.find { x -> x.username == renamedFrom }
        //PLAYER ALWAYS NOT NULL
        player!!.username = username
        player.cards.observe(lcOwner, Observer { cards ->
            //TODO: ? ? ?
            run {
                //TODO: diff util
                if (cards != null) {
                    for (card in cards) {
                        sender.sendMessageTo(player.username, Commands.CLIENT_COMMANDS.CLIENT_GET, card.img)
                    }
                }
            }
        })

        return player
    }

    fun initHand(username: String) {
        val player = players.find { player -> player.username == username }
        val cards = mutableListOf<Card>()
        //no empty in any way
        for (i in 0..Commands.DEFAULT_CARDS_NUM)
            deck.getRandomCard()?.let {
                cards.add(it)
            }

        //вот тут кстати говно
        player?.cards?.postValue(cards as ArrayList<Card>?)
    }

    open fun startGame() {
        leaderPosition = 0
        choosePlayerOrder()
        for (player in players) {
            initHand(player.username)
        }
    }

    private fun choosePlayerOrder() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Sets controller to next user
     */
    private fun setNextLeader() {
        leaderPosition = (leaderPosition + 1) % players.size
        val client = players[leaderPosition]
        sender.sendMessageTo(client.username, Commands.CLIENT_COMMANDS.CLIENT_TURN)
    }

    fun getLeader(): Player {
        return players[leaderPosition]
    }

    abstract fun clientChoosesCard(username: String, choosedCard: String)
}