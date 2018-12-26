package com.example.olesya.boardgames

import android.content.Context
import android.net.wifi.WifiManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View


class Utils {

    companion object {
        fun isWifiEnabled(context: Context): Boolean {
            val wifi = context
                    .applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
            return wifi.isWifiEnabled
        }

        fun showAlert(context: Context, str: String) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(str)
                    .setNeutralButton(R.string.OK, null)
                    .create()
                    .show()
        }

        fun showSnackbar(parentLayout: View, message: String) {
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                    .setAction("CLOSE") { }
                    .setActionTextColor(parentLayout.resources.getColor(android.R.color.holo_red_light))
                    .show()
        }
    }
}