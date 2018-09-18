package com.example.olesya.rxjavatest;

import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.os.Looper.getMainLooper;

public class MenuModel {
    //Wifi-direct
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private BroadcastReceiver p2preceiver;
    private IntentFilter intentFilter;

    //Service vars
    private Intent intent;
    private ServiceConnection serviceConn;
    private boolean serviceBound = false;
    private Server mServer;
    private Client mClient;
    InetAddress hostAddress;
    private MutableLiveData<String> message = new MutableLiveData<>();

    private MutableLiveData<ArrayList<WifiP2pDevice>> availableDevices = new MutableLiveData<>();
    private MutableLiveData<Integer> readyStatus = new MutableLiveData<>();

    public MenuModel(Context context) {
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, getMainLooper(), null);
        readyStatus.setValue(-1);
        initReceiver();

        serviceConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                if (intent.getBooleanExtra(Utils.ACTION_SERVER_SERVICE, false)) {
                    mServer = ((Server.MyBinder) binder).getService();
                    mServer.setMessage(message);
                } else {
                    mClient = ((Client.MyBinder) binder).getService();
                    mClient.setMessage(message);
                }

                serviceBound = true;
            }

            public void onServiceDisconnected(ComponentName name) {
                serviceBound = false;
            }
        };
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
//                    mManager.requestPeers(mChannel, wifiP2pDeviceList ->
//                            availableDevices.setValue(new ArrayList<>(wifiP2pDeviceList.getDeviceList())));
                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                }
            }
        };
    }

    /**
     * Connect via Wifi-Direct to a {@param deviceList} - list of devices;
     * @param context
     * @param deviceList - list of devices to connect
     */
    public void connectToDevices(Context context, ArrayList<WifiP2pDevice> deviceList) {
        WifiP2pDevice device;
        device = deviceList.get(0);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

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

    public void startServiceOnCondition(boolean isServer, Activity activity) {
        if (isServer) {
            intent = new Intent(activity, Server.class);
            intent.putExtra(Utils.ACTION_SERVER_SERVICE, true);
            activity.startService(intent);
        } else {
            intent = new Intent(activity, Client.class);
            intent.putExtra(Utils.ACTION_SERVER_SERVICE, false);
            intent.putExtra(Utils.CLIENT_COMMANDS.HOST_CONFIG, hostAddress);
            activity.startService(intent);
        }

        bindService(activity);
    }

    public void bindService(Context context) {
        if (intent != null)
            context.bindService(intent, serviceConn, BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        if (!serviceBound || intent == null) return;
        context.unbindService(serviceConn);
        serviceBound = false;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Integer> getReady() {
        return readyStatus;
    }
}
