package com.example.olesya.boardgames;

import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

import static android.os.Looper.getMainLooper;

public class MenuModel {
    //Wifi-direct
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private BroadcastReceiver p2preceiver;
    private IntentFilter intentFilter;

    private InetAddress hostAddress;

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
                    //peers discovered
//                    Toast.makeText(context, "peers discovered", Toast.LENGTH_SHORT).show();
                    mManager.requestPeers(mChannel, wifiP2pDeviceList ->
                            availableDevices.setValue(new ArrayList<>(wifiP2pDeviceList.getDeviceList())));
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                    requestInfo(intent);
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                }
            }
        };
    }

    private void requestInfo(Intent intent) {
        if (mManager == null) {
            return;
        }

        NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (networkInfo.isConnected()) {
            // We are connected with the other device, request connection
            // info to find group owner IP
            mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {

                }
            });
        }
    }

    /**
     * Connect via Wifi-Direct to a {@param deviceList} - list of devices;
     * @param context
     * @param deviceList - list of devices to connect
     */
    public void connectToDevices(Context context, ArrayList<WifiP2pDevice> deviceList) {
        if (deviceList == null) {
            return;
        }

        WifiP2pDevice device;
        device = deviceList.get(0);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 0; //not group owner but client

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if (info.groupOwnerAddress!=null) {
                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                            hostAddress = info.groupOwnerAddress;
                        } else {
                            Toast.makeText(context, "Connection failed! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                Toast.makeText(context, "succeed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                //failure logic
            }
        });

    }

    /**
     * Request available peers and set result into {@link #availableDevices}
     */
    public void discoverPeers() {
        mManager.discoverPeers(mChannel, null);
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

    public InetAddress getHostAddress() {
        return hostAddress;
    }
}
