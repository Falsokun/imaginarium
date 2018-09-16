package com.example.olesya.rxjavatest;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class Utils {

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
