package com.example.olesya.rxjavatest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;

public class Utils {
    public static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    public static final String ACTION_SERVER_SERVICE = "ACTION_SERVER_SERVICE";
    public static final String CLIENT_NUM = "CLIENT_NUM";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";
    public static final java.lang.String DELIM = "#";
    public static final String WIN_PTS = "WIN_PTS";

    public class GAME_MODE {
        public static final int UNDEFINED = -1;
        public static final int SCREEN_MODE = 0;
        public static final int CARD_MODE = 1;
    }

    public class CLIENT_CONFIG {
        public static final String HOST_CONFIG = "HOST_CONFIG";
        public static final String ENTER_MSG = "SESSION_START";
        public static final String END_MSG = "SESSION_END";
        public static final String USERNAME = "USERNAME";
        public static final String USERNAME_CHANGED = "USERNAME_CHANGED";
    }

    public class CLIENT_COMMANDS {
        public static final String CLIENT_GET = "CLIENT_GET";
        public static final String CLIENT_WAIT = "CLIENT_WAIT";
        public static final String GAME_START = "GAME_START";
        public static final String CLIENT_MAIN_FINISHED = "CLIENT_MAIN_FIN";
        public static final String CLIENT_MAIN_TURN = "CLIENT_MAIN_TURN";
        public static final String CLIENT_USER_FINISHED = "CLIENT_USER_FIN";
        public static final String CLIENT_USER_TURN = "CLIENT_USER_TURN";
        public static final String CLIENT_MAIN_STOP = "CLIENT_MAIN_STOP";
        public static final String CLIENT_MAIN_STOP_FINISHED = "CLIENT_MAIN_STOP_FINISHED";
        public static final String CLIENT_USER_CHOOSE = "CLIENT_USER_CHOOSE";
        public static final String CLIENT_USER_CHOOSE_FINISHED = "CLIENT_USER_CHOOSE_FINISHED";
    }

    public static boolean isWifiEnabled(@NonNull Context context) {
        WifiManager wifi = (WifiManager)context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        return wifi != null && wifi.isWifiEnabled();
    }

    public static void showAlert(@NonNull Context context, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str)
                .setNeutralButton(R.string.OK, null)
                .create()
                .show();
    }
}
