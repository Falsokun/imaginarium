package com.example.olesya.rxjavatest;

import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.util.ArrayList;

import static android.os.Looper.getMainLooper;

public class MenuModel {
    //Wifi-direct
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private BroadcastReceiver p2preceiver;
    private IntentFilter intentFilter;

    private MutableLiveData<ArrayList<WifiP2pDevice>> availableDevices = new MutableLiveData<>();

    public MenuModel(Context context) {
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, getMainLooper(), null);
        initReceiver();
    }

    /**
     * Initializes data for broadcast receiver and for wifi-direct connection
     */
    public void initReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        p2preceiver = getWifiDirectReceiver();
    }

    private BroadcastReceiver getWifiDirectReceiver() {
        if (p2preceiver != null)
            return p2preceiver;

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                    // Check to see if Wi-Fi is enabled and notify appropriate activity
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    } else {
                    }
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                    mManager.requestPeers(mChannel, wifiP2pDeviceList ->
                            availableDevices.setValue(new ArrayList<>(wifiP2pDeviceList.getDeviceList())));
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
//                    askRequestPeerChanged();
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                }
            }
        };
    }

    public void connectToDevices(Context context, ArrayList<WifiP2pDevice> deviceList) {
        WifiP2pDevice device;
        device = deviceList.get(0);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                //failure logic
            }
        });

    }

    public void requestPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.requestPeers(mChannel, wifiP2pDeviceList ->
                        availableDevices.setValue(new ArrayList<>(wifiP2pDeviceList.getDeviceList())));
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    public IntentFilter getIntentFilter() {
        return intentFilter;
    }

    public BroadcastReceiver getReceiver() {
        return p2preceiver;
    }

    public MutableLiveData<ArrayList<WifiP2pDevice>> getAvailableDevices() {
        return availableDevices;
    }
}
