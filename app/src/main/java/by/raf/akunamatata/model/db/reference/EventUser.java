package by.raf.akunamatata.model.db.reference;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;

import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.db.DatabaseHelper;

public class EventUser extends DatabaseHelper {

    private static EventUser instance;

    public static EventUser getInstance(Context context) {
        if (instance == null) {
            instance = new EventUser(context);
        }
        return instance;
    }

    EventUser(Context context) {
        super(context);
    }

    @Override
    public void deleteEntry(Entity entity) {

    }

    @Override
    public void updateEntry(Entity entity) {

    }

    @Override
    public void addEntry(Entity entity) {

    }

    synchronized public HashMap<String, Boolean> getEntries(String where, String[] whereArgs) {

        SQLiteDatabase mDatabase = null;
        Cursor cursor = null;
        HashMap<String, Boolean> map = new HashMap<>();

        try {
            mDatabase = getWritableDatabase();
            cursor = mDatabase.query(EntrySet.TABLE_NAME, null, where, whereArgs, null, null, null);
            EventUserWrapper wrapper = new EventUserWrapper(cursor);
            map = wrapper.getEntries();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
        return map;

    }


    public static abstract class EntrySet implements BaseColumns {
        public static final String TABLE_NAME = "user_event";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        public static final String COLUMN_NAME_USER_HERE = "here";
    }

    private class EventUserWrapper extends CursorWrapper {

        public EventUserWrapper(Cursor cursor) {
            super(cursor);
        }

        public HashMap<String, Boolean> getEntries() {
            HashMap<String, Boolean> map = new HashMap<>();
            String user_id = getString(getColumnIndex(EntrySet.COLUMN_NAME_USER_ID));
            Boolean hereNow = getInt(getColumnIndex(EntrySet.COLUMN_NAME_USER_HERE)) != 0;
            map.put(user_id, hereNow);
            return map;
        }
    }
}
