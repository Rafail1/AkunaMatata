package by.raf.akunamatata.model.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.activities.SignInActivity;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.User;

import static android.content.Context.MODE_PRIVATE;


public class UserManager extends Observable {
    private List<Observer> loginListeners;
    private static final int OBSERVABLE_CODE = 1;
    private static UserManager instance;


    private UserManager() {
        loginListeners = new ArrayList<>();
    }

    public String getUID() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getCurrentUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DataProvider.AKUNA_MATATA_PREFERENCES, MODE_PRIVATE);
        User currentUser = new User();
        currentUser.setId(sharedPreferences.getString(User.PREF_ID, null));
        currentUser.setPicture(sharedPreferences.getString(User.PREF_PHOTO, null));
        currentUser.setName(sharedPreferences.getString(User.PREF_NAME, null));
        currentUser.setLastName(sharedPreferences.getString(User.PREF_LAST_NAME, null));
        currentUser.setLat(sharedPreferences.getLong(User.PREF_LAT, 0));
        currentUser.setLon(sharedPreferences.getLong(User.PREF_LON, 0));
        currentUser.setStatus(sharedPreferences.getString(User.PREF_STATUS, null));
        currentUser.setBirthDay(sharedPreferences.getLong(User.PREF_BIRTHDAY, 0));
        currentUser.setSex(sharedPreferences.getInt(User.PREF_GENDER, User.GENDER_HZ));
        currentUser.setFree(sharedPreferences.getInt(User.PREF_FREE, User.FREE));
        currentUser.setDrink(sharedPreferences.getInt(User.PREF_DRINK, 0));
        currentUser.setWant(sharedPreferences.getInt(User.PREF_WANT, 0));
        currentUser.setSmoke(sharedPreferences.getInt(User.PREF_SMOKE, 0));
        currentUser.setRegale(sharedPreferences.getInt(User.PREF_REGALE, 0));
        return currentUser;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        loginListeners.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        loginListeners.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer listener : loginListeners) {
            listener.update(this, OBSERVABLE_CODE);
        }
    }

    public void logout(final Context context) {
        if (NetworkManager.getInstance().isNetworkConnected(context)) {
            Intent intent = new Intent(context, SignInActivity.class);
            intent.putExtra("LOGOUT", true);
            context.startActivity(intent);
        }
    }

    public Integer getCurrentUserMask(Context context) {
        User user = getCurrentUser(context);
        return user.getMask();
    }
}
