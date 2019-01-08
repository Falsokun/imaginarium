package com.example.olesya.boardgames.game.controller

import android.content.Context
import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.Commands.DELIM
import com.example.olesya.boardgames.connection.server.ServerCallback
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player
import com.example.olesya.boardgames.ui.dialog.RoundResultDialog
import java.util.*

/**
 * Handles controlling the Imaginarium game
 * @see GameController for description of main concepts
 */
class ImaginariumController(context: Context,
                            players: MutableList<Player> = mutableListOf(),
                            totalPlayers: Int,
                            winPts: Int,
                            sender: ServerCallback
) : GameController(context, players, totalPlayers, winPts, sender) {

    companion object {
        const val IMAGINARIUM_CARD_NUM = 5
        const val GUESSED_CARD_PTS = 3
        const val NONE_GUESSED_PTS = 2
    }

    /**
     * Votes in pair <User, SupposedLeader>
     */
    private var votes: HashMap<String, String> = hashMapOf()

    /**
     * Manages situation when client picks card; in case of
     * - leader: allows to other users to pick cards
     * - user: checks if everyone picked and then opens cards, starting choosing status
     */
    override fun clientPicksCard(username: String, card: String) {
        if (username == getLeader().username) {
            Log.d("Server", "leader select card select card")
            screenMessage.postValue("leader select card")
            addCard(ImaginariumCard(card, false, username))
            sender.sendMessageExcept(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_TURN)
            //TEST CONFIG
//            sender.sendMessageTo(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_CHOOSE + DELIM + players.size)
        } else {
            Log.d("Server", "user select card")
            screenMessage.postValue("user select card")
            addCard(ImaginariumCard(username, false, username))
            if (screenCards.value.size == players.value.size) {
                sender.sendMessageExcept(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_CHOOSE + DELIM + players.value.size)
            }
        }
    }

    /**
     * Manages situation when all cards are visible and
     */
    override fun clientChoosesCard(username: String, chosenCard: String) {
        val tempPlayer = players.value.find { it -> it.username == username }
        val choice = screenCards.value[Integer.parseInt(chosenCard) - 1]
        votes.put(tempPlayer!!.username, choice.playerName)
        if (votes.size >= players.value.size - 1) {
            screenMessage.postValue("Все выбрали карты")
            finishRound()
        }
    }

    /**
     * Chooses next leader and starts new round
     */
    override fun startRound() {
        super.startRound()
        addCards()
        sender.sendMessageTo(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_TURN)
        sender.sendMessageToAll("Выбор тематики и карты")
    }

    /**
     * Adds to player
     */
    private fun addCards() {
        players.value
                .filter { it.cards.value?.size != IMAGINARIUM_CARD_NUM }
                .forEach {
                    deck.getRandomCard()?.let { randomCard -> it.addCard(randomCard) }
                }
    }

    /**
     * Calculating temporary results and checking if there's a necessity of finishing the game
     */
    override fun finishRound() {
        screenMessage.postValue("Calc results")
        calculateVotes()
        if (hasWinner()) {
            showWinner()
        } else {
            screenActions.postValue(Commands.SCREEN_COMMANDS.SHOW_ROUND_RESULTS)
            screenMessage.postValue("start new round")
        }
    }

    private fun hasWinner(): Boolean {
        return players.value.any { it.score.value > winPts }
    }

    private fun calculateVotes() {
        val votes = votes.values.groupingBy { it }.eachCount()
        val leaderName = getLeader().username
        val leaderScore = votes[leaderName] ?: 0
        var lScore = getLeader().score.value
        when {
            leaderScore == players.value.size - 1 -> {
                Log.d("LGame", "all players guessed")
                lScore = Math.max(lScore - GUESSED_CARD_PTS, 0)
            }
            leaderScore > 0 -> {
                Log.d("LGame", "any player guessed")
                lScore += GUESSED_CARD_PTS + leaderScore
                countPtsForUsers(leaderName, votes)
            }
            else -> {
                Log.d("LGame", "no one guessed")
                lScore = Math.max(lScore - NONE_GUESSED_PTS, 0)
                countPtsForUsers(leaderName, votes)
            }
        }

        getLeader().changeScore(lScore)
    }

    private fun countPtsForUsers(leaderName: String, votes: Map<String, Int>) {
        players.value.filter { it.username != leaderName && votes.containsKey(it.username) }
                .forEach { votes[it.username]?.let { it1 -> it.changeScore(it1) } }
    }

    /**
     * Init users' cards with [IMAGINARIUM_CARD_NUM] cards
     */
    override fun initPlayerHands() {
        players.value.forEach { initHand(it.username, IMAGINARIUM_CARD_NUM) }
    }

    override fun choosePlayerOrder() {
        Collections.shuffle(order)
    }
}