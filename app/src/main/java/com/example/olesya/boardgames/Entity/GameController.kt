package com.example.olesya.boardgames.Entity

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import com.example.olesya.boardgames.Utils
import com.example.olesya.boardgames.interfaces.ScreenCallback
import com.example.olesya.boardgames.interfaces.ServerCallback

/**
 * Players with their cards and scores
 */
//TODO: может быть тогда сделать LiveData для Player компонент? Клиент на них подписывается и вуаля
class GameController
constructor(context: Context, var players: MutableList<Player> = mutableListOf(), val winPts: Int, val sender: ServerCallback) : ScreenCallback {

    var deck: Deck = Deck(context)

    var screenCards: MutableLiveData<MutableList<ImaginariumCard>> = MutableLiveData()

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
//        test()
    }

//    private fun test() {
//        val cards = mutableListOf<ImaginariumCard>()
//        for (i in 0..5) {
//            //TODO:!!!
//            val card = ImaginariumCard(deck.getRandomCard()!!)
//            card.isVisible.onNext(true)
//            cards.add(card)
//        }
//
//        screenCards.postValue(cards)
//    }

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

    override fun onStartNewRound() {
        round = 2
        screenCards.postValue(ArrayList())
    }

    /**
     * Fired after shuffle ends;
     * Reveals cards and starts choosing step
     */
    override fun onShuffleEnd() {
        //TODO: при подписке в адаптере при смене видимости должно будет все перевернуться само
        screenCards.value?.forEach { it -> it.isVisible.onNext(true) }
        step.value = Utils.CLIENT_COMMANDS.CLIENT_USER_CHOOSE
    }

    /**
     *
     */
    //TODO: не понимаю почему у меня выдаются карты в стоп раунд, так никто не делает /недовольство/
    override fun stopRound() {
        if (deck.cardsLeft() < players.size && deck.cardsLeft() % players.size != 0)
            return

        players.forEach { player ->
            //TODO: run вроде как просто вызывает блок, ну то есть обычный foreach
            run {
                val cur = player.cards.value
                val card = deck.getRandomCard()
                if (card != null) {
                    cur?.add(card)
                }

                player.cards.postValue(cur)
            }
        }
    }

    override fun onAddUserEvent(username: String, renamedFrom: String): Player {
        val player = players.find { x -> x.username == renamedFrom }
        //PLAYER ALWAYS NOT NULL
        player!!.username = username
        player.cards.observe(lcOwner, Observer { cards ->
            //TODO: ? ? ?
            run {
                //TODO: diff util
                if (cards != null) {
                    for (card in cards) {
                        sender.sendMessageTo(player.username, Utils.CLIENT_COMMANDS.CLIENT_GET, card.img)
                    }
                }
            }
        })

        players.add(player)
        return player
    }

    override fun onRemoveUserEvent(username: String) {
        //TODO: remove
//        players.removeIf { player -> player.username == clientName }
    }

    override fun initHand(username: String) {
        val player = players.find { player -> player.username == username }
        val cards = mutableListOf<Card>()
        //no empty in any way
        for (i in 0..Utils.DEFAULT_CARDS_NUM)
            deck.getRandomCard()?.let {
                cards.add(it)
            }

        //вот тут кстати говно
        player?.cards?.postValue(cards as ArrayList<Card>?)
    }

    fun startGame() {
        choosePlayerOrder()
        for (player in players) {
            initHand(player.username)
        }

        setNextLeader()
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
        sender.sendMessageTo(client.username, Utils.CLIENT_COMMANDS.CLIENT_MAIN_TURN)
    }
}