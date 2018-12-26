package com.example.olesya.boardgames

/**
 * TODO: Нужно будет сделать что-то типа парсера
 */
object Commands {

    const val ACTION_SERVER_SERVICE = "ACTION_SERVER_SERVICE"
    const val CLIENT_NUM = "CLIENT_NUM"
    const val ERR_UNKNOWN = "ERR_UNKNOWN"
    const val DELIM = "#"
    const val WIN_PTS = "WIN_PTS"

    const val NO_ONE_GUESSED_PTS = -2
    const val GUESSED_PTS = 3
    const val DEFAULT_CARDS_NUM = 5

    object GAME_MODE {
        const val UNDEFINED = -1
        const val SCREEN_MODE = 0
        const val CARD_MODE = 1
    }

    object CLIENT_CONFIG {
        const val HOST_CONFIG = "HOST_CONFIG"
        const val ENTER_MSG = "SESSION_START"
        const val END_MSG = "SESSION_END"
        const val USERNAME = "USERNAME"
        const val USERNAME_CHANGED = "USERNAME_CHANGED"
    }

    object CLIENT_COMMANDS {
        const val CLIENT_GET = "CLIENT_GET"
        const val CLIENT_TURN = "CLIENT_USER_TURN"
        const val CLIENT_CHOOSE = "CLIENT_CHOOSE"


        const val CLIENT_WAIT = "CLIENT_WAIT"
        const val GAME_START = "GAME_START"
        const val CLIENT_MAIN_FINISHED = "CLIENT_MAIN_FIN"
        const val CLIENT_MAIN_TURN = "CLIENT_MAIN_TURN"
        const val CLIENT_MAIN_STOP = "CLIENT_MAIN_STOP"
        const val CLIENT_MAIN_STOP_FINISHED = "CLIENT_MAIN_STOP_FINISHED"
        const val CLIENT_USER_CHOOSE = "CLIENT_USER_CHOOSE"
        const val CLIENT_USER_CHOOSE_FINISHED = "CLIENT_USER_CHOOSE_FINISHED"
        const val GAME_STOP = "GAME_STOP"
    }

}