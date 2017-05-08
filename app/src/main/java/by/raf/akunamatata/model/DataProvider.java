package by.raf.akunamatata.model;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import by.raf.akunamatata.model.managers.UserManager;

import static by.raf.akunamatata.model.Event.ADDED;
import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;


public class DataProvider extends ServerListener {

    private FirebaseDatabase database;
    private DatabaseReference myRefCategories;
    private DatabaseReference myRefEvents;
    private DatabaseReference myRefUsers;
    private HashMap<String, Category> sCategories;
    private HashMap<String, Event> sEvents;
    private HashMap<String, User> sUsers;
    public ArrayList<Event> mEventList;
    public User currentUser;
    private static DataProvider instance;
    public static final String AKUNA_MATATA_PREFERENCES = "by.raf.akunamatata.PREF";

    public FirebaseDatabase getDatabase() {
        return database;
    }

    private DataProvider(final Context context) {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        myRefCategories = database.getReference("akunamatata/categories");
        myRefEvents = database.getReference("akunamatata/events");
        myRefUsers = database.getReference("akunamatata/users");

        myRefEvents.keepSynced(true);
        myRefCategories.keepSynced(true);
        myRefUsers.keepSynced(true);
        currentUser = UserManager.getInstance().getCurrentUser(context);
        sCategories = new HashMap<>();
        sEvents = new HashMap<>();
        sUsers = new HashMap<>();
        mEventList = new ArrayList<>();
        listen(myRefUsers, User.class, sUsers);
        listen(myRefEvents, Event.class, sEvents);
        listen(myRefCategories, Category.class, sCategories);
//        initData();

    }


    public static DataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new DataProvider(context);
        }
        return instance;
    }

    public void initData() {

        HashMap<String, Boolean> Categories = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            String id = myRefCategories.push().getKey();
            Category category = new Category(id, "Category" + i);
            sCategories.put(id, category);
            Categories.put(id, true);
        }
        sendCategories(sCategories);
        final long allDates = System.currentTimeMillis();


        HashMap<String, Event> startMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String id = myRefEvents.push().getKey();
            Event event = new Event();
            event.setId(id);
            event.setAuthor("Place N" + i);
            event.setTitle("Event" + i);
            event.setPicture("https://img.afisha.tut.by/img/340x0s/cover/0f/6/festival-landshaftnoy-arkhitektury-i-dizayna-2017-144834.jpg");
            event.setCategoryIds(Categories);
            event.setAuthor("Raf");
            event.setDateStart(allDates);
            event.setDateEnd(allDates + 10000);
            event.setDescription("Description Description Description Description Description Description Description Description");
            startMap.put(id, event);
        }
        sendEvents(startMap);


    }

    public ArrayList<Event> getEventList() {
        return mEventList;
    }

    private void sendCategories(HashMap<String, Category> sCategories) {
        myRefCategories.setValue(sCategories);
    }

    private void sendEvents(Map<String, Event> start) {
        myRefEvents.setValue(start);
    }

    private void sendUsers(HashMap<String, User> start) {
        myRefUsers.setValue(start);
    }

    public void notifyObservers(int observerId, int action, int pos) {
        for (Observer observer : mObservers) {
            int[] args = new int[]{observerId, action, pos};
            observer.update(this, args);
        }

    }


    @Override
    protected <T extends Entity> void onAdded(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            if (mEventList.indexOf(entity) == -1) {
                mEventList.add((Event) entity);
                notifyObservers(Event.OBSERVER_ID, ADDED, -1);
            }
        }
    }

    @Override
    protected <T extends Entity> void onRemoved(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            for (int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(entity.getId())) {
                    mEventList.remove(i);
                    notifyObservers(Event.OBSERVER_ID, REMOVED, i);
                    break;
                }
            }
        }
    }

    @Override
    protected <T extends Entity> void onChanged(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            for (int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(entity.getId())) {
                    mEventList.set(i, (Event) entity);
                    notifyObservers(Event.OBSERVER_ID, CHANGED, i);
                    break;
                }
            }

        }
    }

    public void updateEntity(Entity entity) {
        if (entity instanceof Event) {
            Event event = (Event) entity;
            Map<String, Object> map = new HashMap<>();
            map.put(entity.getId(), event);
            myRefEvents.updateChildren(map);
        }
    }

    public void updateCurrentUser(Context context) {
        currentUser = UserManager.getInstance().getCurrentUser(context);
        myRefUsers.child(currentUser.getId()).setValue(currentUser);
    }
}
