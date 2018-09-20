package com.example.olesya.rxjavatest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;

public class Utils {

    public static final String ACTION_SERVER_SERVICE = "ACTION_SERVER_SERVICE";
    public static final String CLIENT_NUM = "CLIENT_NUM";
    public static final String ERR_UNKNOWN = "ERR_UNKNOWN";
    public static final java.lang.String DELIM = "#";

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
    }

    public class CLIENT_COMMANDS {
        public static final String SELECTED = "SELECTED";
        public static final String CLIENT_CHOOSE = "CLIENT_CHOOSE";
        public static final String CLIENT_GET = "CLIENT_GET";
        public static final String CLIENT_WAIT = "CLIENT_WAIT";
        public static final String CLIENT_TURN = "CLIENT_TURN";
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

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
