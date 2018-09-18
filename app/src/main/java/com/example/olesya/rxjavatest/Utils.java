package com.example.olesya.rxjavatest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class Utils {

    public static final String ACTION_SERVER_SERVICE = "ACTION_SERVER_SERVICE";
    public static final String CLIENT_NUM = "CLIENT_NUM";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";

    class GAME_MODE {
        public static final int UNDEFINED = -1;
        public static final int SCREEN_MODE = 0;
        public static final int CARD_MODE = 0;
    }

    class CLIENT_COMMANDS {
        public static final String HOST_CONFIG = "HOST_CONFIG";
        public static final String ENTER_MSG = "SESSION_START";
        public static final String END_MSG = "SESSION_END";
    }

    public static boolean isWifiEnabled(@NonNull Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static void showAlert(@NonNull Context context, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(str)
                .setNeutralButton(R.string.OK, null)
                .create()
                .show();
    }
}
