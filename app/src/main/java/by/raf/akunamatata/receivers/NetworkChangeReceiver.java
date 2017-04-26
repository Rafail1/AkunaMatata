package by.raf.akunamatata.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import by.raf.akunamatata.model.GlobalVars;

/**
 * Created by raf on 4/22/17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    private GlobalVars mGlobalVars;
    private static boolean connected = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        mGlobalVars = (GlobalVars) context.getApplicationContext();
        if (mGlobalVars.mNetworkManager.isNetworkConnected()) {
            if(!connected) {
                connected = true;
                mGlobalVars.mNetworkManager.onNetworkAvailable();
            }
        } else {
            if(connected) {
                connected = false;
                mGlobalVars.mNetworkManager.onLostNetwork();
            }
        }

    }

}