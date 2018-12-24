package com.example.olesya.boardgames

/**
 * TODO: Нужно будет сделать что-то типа парсера
 */
class Commands {

    object CLIENT_COMMANDS {
        val CLIENT_GET = "CLIENT_GET"
        val CLIENT_WAIT = "CLIENT_WAIT"
        val GAME_START = "GAME_START"
        val CLIENT_MAIN_FINISHED = "CLIENT_MAIN_FIN"
        val CLIENT_MAIN_TURN = "CLIENT_MAIN_TURN"
        val CLIENT_USER_FINISHED = "CLIENT_USER_FIN"
        val CLIENT_USER_TURN = "CLIENT_USER_TURN"
        val CLIENT_MAIN_STOP = "CLIENT_MAIN_STOP"
        val CLIENT_MAIN_STOP_FINISHED = "CLIENT_MAIN_STOP_FINISHED"
        val CLIENT_USER_CHOOSE = "CLIENT_USER_CHOOSE"
        val CLIENT_USER_CHOOSE_FINISHED = "CLIENT_USER_CHOOSE_FINISHED"
        val GAME_STOP = "GAME_STOP"
    }

}