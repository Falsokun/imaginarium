package com.example.olesya.rxjavatest;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olesya.rxjavatest.view.CardActivity;
import com.example.olesya.rxjavatest.view.ScreenActivity;

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
//        model.getMessage().observe(weakFragment.get(), v -> Utils.showAlert(weakFragment.get().getContext(),
//                model.getMessage().getValue()));
    }

    private void startGame(Context context, String username, int gameMode, int playerNum, int ptsToWin) {
        if (gameMode == Utils.GAME_MODE.SCREEN_MODE) {
            Intent intent = new Intent(context, ScreenActivity.class);
            intent.putExtra(Utils.CLIENT_NUM, playerNum);
            intent.putExtra(Utils.WIN_PTS, ptsToWin);
            context.startActivity(intent);
        } else if (gameMode == Utils.GAME_MODE.CARD_MODE) {
            Intent intent = new Intent(context, CardActivity.class);
            intent.putExtra(Utils.CLIENT_CONFIG.HOST_CONFIG, model.getHostAddress());
            intent.putExtra(Utils.CLIENT_CONFIG.USERNAME, username);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "choosen wrong mode", Toast.LENGTH_SHORT).show();
        }
    }

    public View.OnClickListener getOnSearchClickListener(Activity context, MenuFragment fragment) {
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Utils.PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            } else {
                model.discoverPeers();
            }
        };
    }

    public void requestPeers() {
        model.discoverPeers();
    }

    private void updateListInDialog() {
        if (dialogView == null || model.getAvailableDevices().getValue() == null) {
            return;
        }

        peerDialogAdapter.clear();
        ArrayList<String> titles = new ArrayList<>();
        for (WifiP2pDevice device : model.getAvailableDevices().getValue()) {
            titles.add(device.deviceName);
        }

        peerDialogAdapter.addAll(titles);
        peerDialogAdapter.notifyDataSetChanged();
        dialogView.findViewById(R.id.progress).setVisibility(View.GONE);
        if (titles.size() == 0) {
            ((TextView) dialogView.findViewById(R.id.title)).setText(R.string.nothing_found);
        } else {
            ((TextView) dialogView.findViewById(R.id.title)).setText(R.string.found_devices);
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

    public View.OnClickListener getOnStartClickListener(Switch switcher, TextView username,
                                                        TextView players, TextView pts) {
        return v -> {
            int mode = switcher.isChecked() ?
                    Utils.GAME_MODE.SCREEN_MODE : Utils.GAME_MODE.CARD_MODE;
            int personsNum = Integer.valueOf(players.getText().toString());
            if (personsNum > 5 || personsNum < 2) {
                Toast.makeText(v.getContext(), R.string.err_persons_num, Toast.LENGTH_SHORT).show();
                return;
            }

            int winPts = Integer.valueOf(pts.getText().toString());
            startGame(v.getContext(), username.getText().toString(), mode, personsNum, winPts);
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

    public void onResume() {
        weakFragment.get().getActivity().registerReceiver(model.getReceiver(), model.getIntentFilter());

//        weakFragment.get().getActivity().bindService(serviceIntent, sConn, Context.BIND_AUTO_CREATE);
    }

    public void onPause() {
        weakFragment.get().getActivity().unregisterReceiver(model.getReceiver());
    }

    public void discoverPeers() {
        model.discoverPeers();
    }
}
