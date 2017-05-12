package by.raf.akunamatata.model;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public abstract class ServerListener extends Observable {
    List<Observer> mObservers = new ArrayList<>();

    protected abstract <T extends Entity> void onAdded(Entity entity, HashMap<String, T> map);
    protected abstract <T extends Entity> void onRemoved(Entity entity, HashMap<String, T> map);
    protected abstract <T extends Entity> void onChanged(Entity entity, HashMap<String, T> map);

    private <T extends Entity> void add(T entity, HashMap<String, T> map) {
        map.put(entity.getId(), entity);
        onAdded(entity, map);
    }

    private <T extends Entity> void change(T entity, HashMap<String, T> map) {
        for (Object o : map.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getKey().equals(entity.getId())) {
                map.put(entity.getId(), entity);
                break;
            }
        }
        onChanged(entity, map);
    }

    private <T extends Entity> void remove(T entity, HashMap<String, T> map) {
        for (Object o : map.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getKey().equals(entity.getId())) {
                map.remove(entity.getId());
                break;
            }
        }
        onRemoved(entity, map);
    }

    public <T extends Entity> ChildEventListener getListener(final Class<T> EntityClass,
                                                             final HashMap<String, T> map) {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                T entity = dataSnapshot.getValue(EntityClass);
                String id = dataSnapshot.getKey();
                entity.setId(id);
                add(entity, map);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                T entity = dataSnapshot.getValue(EntityClass);
                entity.setId(dataSnapshot.getKey());
                change(entity, map);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                T entity = dataSnapshot.getValue(EntityClass);
                entity.setId(dataSnapshot.getKey());
                remove(entity, map);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        };
    }

    @Override
    public synchronized void addObserver(Observer o) {
        mObservers.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        mObservers.remove(o);
    }

}
