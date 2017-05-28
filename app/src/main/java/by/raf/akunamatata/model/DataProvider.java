package by.raf.akunamatata.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import by.raf.akunamatata.activities.WithToolbarActivity;
import by.raf.akunamatata.model.managers.UserManager;

import static android.content.Context.MODE_PRIVATE;
import static by.raf.akunamatata.model.Event.ADDED;
import static by.raf.akunamatata.model.Event.CHANGED;
import static by.raf.akunamatata.model.Event.REMOVED;


public class DataProvider extends ServerListener {

    public static String currentCategory;
    private FirebaseDatabase database;
    private DatabaseReference myRefCategories;
    public DatabaseReference myRefEvents;
    private DatabaseReference myRefUsers;
    public ArrayList<Category> mCategories;
    public HashMap<String, Event> sEvents;
    private HashMap<String, User> sUsers;
    public ArrayList<Event> mEventList;
    private static DataProvider instance;

    public static final String AKUNA_MATATA_PREFERENCES = "by.raf.akunamatata.PREF";

    public FirebaseDatabase getDatabase() {
        return database;
    }

    private DataProvider() {

        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        myRefCategories = database.getReference("akunamatata/categories");
        myRefEvents = database.getReference("akunamatata/events");
        myRefUsers = database.getReference("akunamatata/users");

        myRefEvents.keepSynced(true);
        myRefCategories.keepSynced(true);
        myRefUsers.keepSynced(true);
        mCategories = new ArrayList<>();

        sEvents = new HashMap<>();
        sUsers = new HashMap<>();
        mEventList = new ArrayList<>();
//        initData();

    }


    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }

    public void initData() {

        HashMap<String, Boolean> Categories = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            String id = myRefCategories.push().getKey();
            Category category = new Category(id, "Category" + i);
            mCategories.add(category);
            Categories.put(id, true);
        }
        sendCategories(mCategories);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        final long allDates = today.getTimeInMillis();


        HashMap<String, Event> startMap = new HashMap<>();

        String img1 = "https://img.afisha.tut.by/img/340x0s/cover/00/c/roboticon-2017-vystavka-robototekhniki-i-innovacionnykh-tekhnologiy-3056983.jpg";
        String img2 = "https://img.afisha.tut.by/img/340x0s/cover/0f/6/festival-landshaftnoy-arkhitektury-i-dizayna-2017-144834.jpg";
        for (int i = 0; i < 10; i++) {
            String id = myRefEvents.push().getKey();
            Event event = new Event();
            event.setId(id);
            event.setAuthor("Place N" + i);
            event.setTitle("Event" + i);
            event.setPicture((i%2 ==0 ? img1 : img2));
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

    private void sendCategories(ArrayList<Category> sCategories) {
        HashMap<String, Category> hm = new HashMap<>();
        for (int i = 0; i < sCategories.size(); i++) {
            hm.put(sCategories.get(i).getId(), sCategories.get(i));
        }
        myRefCategories.setValue(hm);
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
            Event newEvent = (Event) entity;
            if(currentCategory != null && !newEvent.getCategoryIds().containsKey(currentCategory)) {
                return;
            }

            for (int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(newEvent.getId())) {
                   return;
                }
            }
            mEventList.add(newEvent);
            notifyObservers(Event.OBSERVER_ID, ADDED, -1);
        }
    }

    @Override
    protected <T extends Entity> void onRemoved(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            Event newEvent = (Event) entity;
            if(currentCategory != null && !newEvent.getCategoryIds().containsKey(currentCategory)) {
                return;
            }
            for (int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(newEvent.getId())) {
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
            Event newEvent = (Event) entity;
            if(currentCategory != null && !newEvent.getCategoryIds().containsKey(currentCategory)) {
                return;
            }
            for (int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(newEvent.getId())) {
                    mEventList.set(i, newEvent);
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
        User currentUser = UserManager.getInstance().getCurrentUser(context);
        myRefUsers.child(currentUser.getId()).setValue(currentUser);
    }

    public void getCategories() {
        myRefCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCategories.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    mCategories.add(category);
                }

                notifyObservers(Category.OBSERVER_ID, ADDED, -1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
