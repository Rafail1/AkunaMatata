package by.raf.akunamatata.model.managers;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.receivers.NetworkChangeReceiver;

/**
 * Created by raf on 4/23/17.
 */

public class NetworkManager extends Observable {
    private static final int OBSERVABLE_CODE = 0;
    public static boolean connected;
    private List<Observer> mObservers;
    private Context mContext;
    private NetworkChangeReceiver mNetworkChangeReceiver;
    private IntentFilter mFilter;
    public NetworkManager(Context context) {
        mContext = context;
        connected = isNetworkConnected();
        mObservers = new ArrayList<>();
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
    }

    public void registerReceiver(Context context) {
        context.registerReceiver(mNetworkChangeReceiver, mFilter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mNetworkChangeReceiver);
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public void onNetworkAvailable() {
        connected = true;
        notifyObservers();
    }

    @Override
    public synchronized void addObserver(Observer o) {
        mObservers.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        mObservers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : mObservers) {
            observer.update(this, OBSERVABLE_CODE);
        }
    }

    public void onLostNetwork() {
        connected = false;
        notifyObservers();
    }

}
