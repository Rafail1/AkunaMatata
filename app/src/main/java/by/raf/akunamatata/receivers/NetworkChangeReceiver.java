package by.raf.akunamatata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.managers.NetworkManager;


public class NetworkChangeReceiver extends BroadcastReceiver {
    private static boolean connected = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (NetworkManager.getInstance().isNetworkConnected(context)) {
            if(!connected) {
                connected = true;
                NetworkManager.getInstance().onNetworkAvailable();
            }
        } else {
            if(connected) {
                connected = false;
                NetworkManager.getInstance().onLostNetwork();
            }
        }

    }

}