package by.raf.akunamatata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import by.raf.akunamatata.model.GlobalVars;
import by.raf.akunamatata.model.managers.NetworkManager;


public class NetworkChangeReceiver extends BroadcastReceiver {
    private GlobalVars mGlobalVars;
    private static boolean connected = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        mGlobalVars = (GlobalVars) context.getApplicationContext();
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