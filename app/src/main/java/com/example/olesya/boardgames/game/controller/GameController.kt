package com.example.olesya.boardgames.game.controller

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import android.content.Context
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.NonNullMutableLiveData
import com.example.olesya.boardgames.connection.server.ServerCallback
import com.example.olesya.boardgames.entity.Card
import com.example.olesya.boardgames.entity.Deck
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player

/**
 * Manages all game logic;
 * Showing messages to
 * Players with their cards and scores
 */
abstract class GameController constructor(context: Context,
                                          players: MutableList<Player> = mutableListOf(),
                                          val totalPlayers: Int,
                                          val winPts: Int,
                                          val sender: ServerCallback
) {

    /**
     * Deck with cards
     */
    var deck: Deck = Deck(context)

    /**
     * Cards visible at the screen
     */
    var screenCards: NonNullMutableLiveData<MutableList<ImaginariumCard>> = NonNullMutableLiveData(mutableListOf())

    val players: NonNullMutableLiveData<MutableList<Player>> = NonNullMutableLiveData(mutableListOf())

    /**
     * Message to show to device
     */
    var screenMessage: MutableLiveData<String> = MutableLiveData()

    val screenActions: MutableLiveData<String> = MutableLiveData()

    /**
     * Position of user who is able to finish the round
     * Ex.: Imaginarium - who sets the topic
     * Ex.: Fool - who starts to put cards
     */
    var leaderPosition: Int = -1

    lateinit var lcOwner: LifecycleOwner

    var order = MutableList(players.size) { i -> i }

    init {
        this.players.value = players
    }

    //region abstracts
    /**
     * Client picked card and put to desk
     */
    abstract fun clientPicksCard(username: String, card: String)

    /**
     * Finishes round
     */
    abstract fun finishRound()

    /**
     * Client chooses card among visible on {@link #Desk}
     */
    abstract fun clientChoosesCard(username: String, chosenCard: String)

    /**
     * Init cards in users' hands
     */
    abstract fun initPlayerHands()

    abstract fun choosePlayerOrder()
    //endregion

    fun addCard(card: ImaginariumCard) {
        val cur = screenCards.value
        //won't be null if null arraylist won't be explicitly set to screencards
        cur.add(0, card)
        screenCards.postValue(cur)
    }

    fun onAddUserEvent(username: String) {
        val newPlayer = Player("id_$username")
        if (players.value.any { it.username == "id_$username" }) {
            newPlayer.username += "0"
            sender.sendMessageTo(username, Commands.CLIENT_CONFIG.USERNAME, newPlayer.username)
        }

        sender.changeClientName(username, newPlayer.username)
        //PLAYER ALWAYS NOT NULL
        addPlayer(newPlayer)
        newPlayer.cards.observe(lcOwner, Observer { cards ->
            if (cards != null) {
                for (card in cards) {
                    sender.sendMessageTo(newPlayer.username, Commands.CLIENT_COMMANDS.CLIENT_GET, card.img)
                }
            }
        })
    }

    /**
     * Initializes hand of {@param username} with random cards from desk
     */
    fun initHand(username: String, cardNumber: Int) {
        val player = players.value.find { player -> player.username == username }
        val cards = mutableListOf<Card>()
        //no empty in any way
        for (i in 0..cardNumber)
            deck.getRandomCard()?.let {
                cards.add(it)
            }

        //вот тут кстати говно
        player?.cards?.postValue(cards as ArrayList<Card>?)
    }

    open fun startGame() {
        deck.reInitDeck()
        choosePlayerOrder()
        initPlayerHands()
        startRound()
    }

    /**
     * Need to be overridden in child class!!
     */
    open fun startRound() {
        setNextLeader()
        screenCards.postValue(mutableListOf())
    }

    /**
     * Chooses next leader
     */
    private fun setNextLeader() {
        leaderPosition = (leaderPosition + 1) % players.value.size
    }

    fun getLeader(): Player {
        return players.value[leaderPosition]
    }

    private fun addPlayer(username: Player) {
        val temp = players.value
        temp.add(username)
        players.postValue(temp)
    }

    fun onRemovePlayer(username: String) {
        val temp = players.value
        temp.removeAll { it.username == username }
        players.postValue(temp)
        sender.removeClient(username)
    }

    fun showWinner() {
        screenActions.postValue(Commands.SCREEN_COMMANDS.SHOW_WINNER)
    }

    fun test() {
        deck.reInitDeck()
        val list: MutableList<ImaginariumCard> = mutableListOf()
        for (i in 1..5) {
            val card: Card = deck.getRandomCard() ?: return
                list.add(ImaginariumCard(card))
        }

        screenCards.postValue(list)
    }
}