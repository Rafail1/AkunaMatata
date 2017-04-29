package by.raf.akunamatata.model.db.reference;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;

import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.db.DatabaseHelper;


public class EventCategory extends DatabaseHelper {

    private static EventCategory instance;


    public static EventCategory getInstance(Context context) {
        if (instance == null) {
            instance = new EventCategory(context);
        }
        return instance;
    }

    private EventCategory(Context context) {
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

    public void addRefs(Event event) {

    }

    synchronized public HashMap<String, Boolean> getEntries(String where, String[] whereArgs) {
        Cursor cursor = null;
        SQLiteDatabase mDatabase = null;
        HashMap<String, Boolean> map = new HashMap<>();
        try {
            mDatabase = getWritableDatabase();
            cursor = mDatabase.query(EntrySet.TABLE_NAME, null, where, whereArgs,
                    null, null, null);
            EventCategoryWrapper wrapper = new EventCategoryWrapper(cursor);
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
        public static final String TABLE_NAME = "event_category";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
    }

    private class EventCategoryWrapper extends CursorWrapper {
        public EventCategoryWrapper(Cursor cursor) {
            super(cursor);
        }

        public HashMap<String, Boolean> getEntries() {
            HashMap<String, Boolean> map = new HashMap<>();
            String user_id = getString(getColumnIndex(EntrySet.COLUMN_NAME_EVENT_ID));
            map.put(user_id, true);
            return map;
        }
    }

}
