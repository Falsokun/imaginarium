package com.example.olesya.rxjavatest;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MenuViewModel extends ViewModel {

    private MenuModel model;
    private View dialogView;

    private WeakReference<MenuFragment> weakFragment;
    private ArrayAdapter<String> peerDialogAdapter;

    //фрагмент привязывать нельзя, надо переделать через привязку сервиса
    public MenuViewModel(MenuFragment menuFragment) {
        model = new MenuModel(menuFragment.getContext());
        weakFragment = new WeakReference<>(menuFragment);
        model.getAvailableDevices().observe(weakFragment.get(), deviceList -> updateListInDialog());
    }

    public View.OnClickListener getOnSearchClickListener(Activity context) {
        return v -> {
            if (!Utils.isWifiEnabled(context)) {
                Utils.showAlert(context, context.getString(R.string.wifi_disabled_retry));
                return;
            }

            initDialog(context)
                    .setPositiveButton(R.string.OK, (dialogInterface, i) ->
                            model.connectToDevices(context, getCheckedList(dialogView.findViewById(R.id.list))))
                    .setNeutralButton(R.string.cancel, null)
                    .create()
                    .show();

            model.requestPeers();
        };
    }

    private void updateListInDialog() {
        if (dialogView == null || model.getAvailableDevices().getValue() == null) {
            return;
        }

        peerDialogAdapter.clear();
        ArrayList<String> titles = new ArrayList<>();
        for(WifiP2pDevice device : model.getAvailableDevices().getValue()) {
            titles.add(device.deviceName);
        }

        peerDialogAdapter.addAll(titles);
        peerDialogAdapter.notifyDataSetChanged();
        dialogView.findViewById(R.id.progress).setVisibility(View.GONE);
        if (titles.size() == 0) {
            ((TextView)dialogView.findViewById(R.id.title)).setText("nothing found");
        } else {
            ((TextView)dialogView.findViewById(R.id.title)).setText("found devices:");
        }
    }

    private AlertDialog.Builder initDialog(Activity activity) {
        dialogView = activity.getLayoutInflater()
                .inflate(R.layout.dialog_searching, null);
        ListView peersLv = dialogView.findViewById(R.id.list);
        peerDialogAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_list_item_multiple_choice);
        peersLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        peersLv.setAdapter(peerDialogAdapter);
        return new AlertDialog.Builder(activity)
                .setTitle(R.string.connecting)
                .setView(dialogView);
    }

    public View.OnClickListener getOnStartClickListener() {
        return v -> {

        };
    }

    private ArrayList<WifiP2pDevice> getCheckedList(ListView listView) {
        ArrayList<WifiP2pDevice> deviceList = new ArrayList<>();
        if (listView.getCheckedItemCount() == 0) {
            return null;
        }

        SparseBooleanArray checkedArray = listView.getCheckedItemPositions();
        for (int i = 0; i < model.getAvailableDevices().getValue().size(); i++) {
            if (checkedArray.get(i)) {
                deviceList.add(model.getAvailableDevices().getValue().get(i));
            }
        }

        return deviceList;
    }

    public void startServiceOnCondition(boolean isChecked, Activity activity) {
        if (isChecked) {
//            activity.bindService(service)
            activity.startService(new Intent(activity, Server.class));
        } else {
            activity.startService(new Intent(activity, Client.class));
        }
    }

    public void onResume() {
        weakFragment.get().getActivity().registerReceiver(model.getReceiver(), model.getIntentFilter());
//        weakFragment.get().getActivity().bindService(serviceIntent, sConn, Context.BIND_AUTO_CREATE);
    }

    public void onPause() {
        weakFragment.get().getActivity().unregisterReceiver(model.getReceiver());
//        if (bound) {
//            getActivity().unbindService(sConn);
//            bound = false;
//        }
    }
}
