package com.example.olesya.boardgames.game.controller

import android.content.Context
import android.util.Log
import com.example.olesya.boardgames.Commands
import com.example.olesya.boardgames.connection.server.ServerCallback
import com.example.olesya.boardgames.entity.Card
import com.example.olesya.boardgames.entity.GameController
import com.example.olesya.boardgames.entity.ImaginariumCard
import com.example.olesya.boardgames.entity.Player

class ImaginariumController(context: Context,
                            players: MutableList<Player> = mutableListOf(),
                            winPts: Int,
                            sender: ServerCallback
) : GameController(context, players, winPts, sender) {

    //main confirm - можно ввести, если я хочу оставить ту же логику
    enum class State {
        ROUND_START, ROUND_END, MAIN_TURN, USER_TURN, USER_CHOOSE, MAIN_CONFIRM
    }

    var currentState: State = State.ROUND_START

    var votes: HashMap<String, String> = hashMapOf()

    override fun startGame() {
        super.startGame()
        sender.sendMessageTo(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_TURN)
        sender.sendMessageToAll("Выбор тематики и карты")
        currentState = State.MAIN_TURN
    }

    override fun clientPicksCard(username: String, card: String) {
        if (username == getLeader().username) {
            Log.d("Server", "leader select card select card")
            screenMessage.postValue("leader select card")
            currentState = State.USER_TURN
            addCard(ImaginariumCard(card, false, username))
            sender.sendMessageExcept(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_TURN)
        } else {
            Log.d("Server", "user select card")
            screenMessage.postValue("user select card")
            addCard(ImaginariumCard(username, false, username))
            if (screenCards.value?.size == players.size) {
                currentState = State.USER_CHOOSE
                sender.sendMessageExcept(getLeader().username, Commands.CLIENT_COMMANDS.CLIENT_CHOOSE)
            }
        }
    }

    override fun clientChoosesCard(username: String, choosedCard: String) {
        val tempPlayer = players.find { it -> it.username == username }
        val choice = screenCards.value?.find { it -> it.playerName == choosedCard }
        votes.put(tempPlayer!!.username, choice!!.playerName)
        if (votes.size == players.size - 1) {
            screenMessage.postValue("Все выбрали карты")
        }
    }
}