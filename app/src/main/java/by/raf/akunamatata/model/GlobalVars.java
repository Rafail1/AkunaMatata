package by.raf.akunamatata.model;

import android.app.Application;

import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;

/**
 * Created by raf on 4/22/17.
 */

public class GlobalVars extends Application {
    public UserManager mUserManager;
    public NetworkManager mNetworkManager;
    public Server mServer;
    @Override
    public void onCreate() {
        super.onCreate();
        mNetworkManager = new NetworkManager(getApplicationContext());
        mUserManager = new UserManager();
        mServer = new Server();
    }

}
