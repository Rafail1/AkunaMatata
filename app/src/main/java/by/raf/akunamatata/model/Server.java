package by.raf.akunamatata.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;

/**
 * Created by raf on 4/22/17.
 */

public class Server extends ServerListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRefCategories = database.getReference("akunamatata/categories");
    private DatabaseReference myRefEvents = database.getReference("akunamatata/events");
    private DatabaseReference myRefUsers = database.getReference("akunamatata/users");
    private HashMap<String, Category> sCategories;
    private HashMap<String, Event> sEvents;
    private HashMap<String, User> sUsers;


    private ArrayList<Event> mEventList;


    public Server() {
        sCategories = new HashMap<>();
        sEvents = new HashMap<>();
        sUsers = new HashMap<>();
        mEventList = new ArrayList<>();
    }

    public void init() {
        sEvents.clear();
        sUsers.clear();
        sCategories.clear();
        listen(myRefUsers, User.class, sUsers);
        listen(myRefEvents, Event.class, sEvents);
        listen(myRefCategories, Category.class, sCategories);
// trash
        /*HashMap<String, Boolean> Categories = new HashMap<>();

        for(int i = 0; i < 3; i++) {
            String id = myRefCategories.push().getKey();
            Category category = new Category(id, "Category" + i);
            sCategories.put(id, category);
            Categories.put(id,true);
        }
        sendCategories(sCategories);

        HashMap<String, Boolean> users = new HashMap<>();
        for(int i = 0; i < 10; i++) {
            String id = myRefUsers.push().getKey();
            User user = new User(id, i % 2, "Status" + i, new Location("service Provider"));
            user.setId(id);
            sUsers.put(id, user);
            users.put(user.getId(), i % 2 == 0);
        }
        sendUsers(sUsers);

        final long allDates = System.currentTimeMillis();


        HashMap<String, Event> startMap = new HashMap<>();
        for(int i = 0; i < 10; i++) {
            String id = myRefEvents.push().getKey();
            //String author, String title, String description, String pictureUri, DateStart, DateEnd
            Event event = new Event(id, "Title"+i, "Description"+i,
                    "https://img.afisha.tut.by/img/340x0s/cover/08/10/gulyandiya-430352.jpg",
                    Categories,
                    allDates, allDates, users);
            startMap.put(id, event);
        }
        sendEvents(startMap);*/


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

    @Override
    protected <T extends Entity> void onAdded(Entity entity, HashMap<String, T> map) {
        if (entity instanceof Event) {
            mEventList.add((Event) entity);
            notifyObservers(Event.class);
        }
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
    }
}
