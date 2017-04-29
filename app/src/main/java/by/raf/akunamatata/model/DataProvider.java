package by.raf.akunamatata.model;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import by.raf.akunamatata.model.db.CategoryHelper;
import by.raf.akunamatata.model.db.EventTableHelper;
import by.raf.akunamatata.model.db.UserTableHelper;
import by.raf.akunamatata.model.db.reference.EventCategory;
import by.raf.akunamatata.model.db.reference.EventUser;

import static by.raf.akunamatata.model.db.DatabaseHelper.ACTION_ADD;
import static by.raf.akunamatata.model.db.DatabaseHelper.ACTION_DELETE;
import static by.raf.akunamatata.model.db.DatabaseHelper.ACTION_UPDATE;


public class DataProvider extends ServerListener {
    private FirebaseDatabase database;
    private DatabaseReference myRefCategories;
    private DatabaseReference myRefEvents;
    private DatabaseReference myRefUsers;
    private HashMap<String, Category> sCategories;
    private HashMap<String, Event> sEvents;
    private HashMap<String, User> sUsers;
    private ArrayList<Event> mEventList;
    private static DataProvider instance;

    private DataProvider(final Context context) {
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        myRefCategories =  database.getReference("akunamatata/categories");
        myRefEvents = database.getReference("akunamatata/events");
        myRefUsers = database.getReference("akunamatata/users");
        sCategories = new HashMap<>();
        sEvents = new HashMap<>();
        sUsers = new HashMap<>();
        mEventList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                sEvents = EventTableHelper.getInstance(context).getAllEntries(null, null);
                sUsers = UserTableHelper.getInstance(context).getAllEntries(null, null);
                sCategories = CategoryHelper.getInstance(context).getAllEntries(null, null);

                listen(myRefUsers, User.class, sUsers);
                listen(myRefEvents, Event.class, sEvents);
                listen(myRefCategories, Category.class, sCategories);
            }
        }).start();

    }
    public static DataProvider getInstance(Context context) {
        if (instance == null) {
            instance = new DataProvider(context);

        }
        return instance;
    }
    public void initData() {

// trash
//        HashMap<String, Boolean> Categories = new HashMap<>();
//
//        for(int i = 0; i < 3; i++) {
//            String id = myRefCategories.push().getKey();
//            Category category = new Category(id, "Category" + i);
//            sCategories.put(id, category);
//            Categories.put(id,true);
//        }
//        sendCategories(sCategories);
//        final long allDates = System.currentTimeMillis();
//        HashMap<String, Boolean> users = new HashMap<>();
//        for(int i = 0; i < 10; i++) {
//            String id = myRefUsers.push().getKey();
//            User user = new User(id, "user" + i, "luser" + i, i % 2, "Status" + i,
//                    allDates, "https://pp.userapi.com/c624731/v624731782/47886/LrF637bGxPw.jpg",
//                    new Location("service Provider"));
//            user.setId(id);
//            sUsers.put(id, user);
//            users.put(user.getId(), i % 2 == 0);
//        }
//        sendUsers(sUsers);
//
//        HashMap<String, Event> startMap = new HashMap<>();
//        for(int i = 0; i < 10; i++) {
//            String id = myRefEvents.push().getKey();
//            //String author, String title, String description, String pictureUri, DateStart, DateEnd
//            Event event = new Event();
//            startMap.put(id, event);
//        }
//        sendEvents(startMap);


    }
    public ArrayList<Event> getEventList() {return mEventList;}

    private void sendCategories(HashMap<String, Category> sCategories) {myRefCategories.setValue(sCategories);}

    private void sendEvents(Map<String, Event> start) {myRefEvents.setValue(start);}

    private void sendUsers(HashMap<String,User> start) {
        myRefUsers.setValue(start);
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Observer observer : mObservers) {
            observer.update(this, arg);
        }

    }

    private void changeBaseEntity(final Entity entity, final int action) {
        for (final Observer observer : mObservers) {
                if(observer instanceof Fragment) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (entity instanceof Event) {
                            EventTableHelper.getInstance(((Fragment) observer).getContext())
                                    .update(action, entity);
                            EventCategory.getInstance(((Fragment) observer).getContext())
                                    .update(action, entity);
                            EventUser.getInstance(((Fragment) observer).getContext())
                                    .update(action, entity);
                        } else if (entity instanceof Category) {
                            CategoryHelper.getInstance(((Fragment) observer).getContext())
                                    .update(action, entity);
                            EventCategory.getInstance(((Fragment) observer).getContext())
                                    .update(action, entity);
                        }
                    }
                }).start();

            }
        }
    }

    @Override
    protected <T extends Entity> void onAdded(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            if(mEventList.indexOf(entity) == -1) {
                mEventList.add((Event) entity);
                notifyObservers(Event.class);
            }
        }
        changeBaseEntity(entity, ACTION_ADD);
    }

    @Override
    protected <T extends Entity> void onRemoved(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            for(int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(entity.getId())) {
                    mEventList.remove(i);
                }
            }
            notifyObservers(Event.class);
        }
        changeBaseEntity(entity, ACTION_DELETE);
    }

    @Override
    protected <T extends Entity> void onChanged(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            for(int i = 0; i < mEventList.size(); i++) {
                if (mEventList.get(i).getId().equals(entity.getId())) {
                    mEventList.set(i, (Event) entity);
                }
            }
            notifyObservers(Event.class);
        }
        changeBaseEntity(entity, ACTION_UPDATE);
    }
}
