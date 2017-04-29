package by.raf.akunamatata.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;

import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.db.reference.EventCategory;
import by.raf.akunamatata.model.db.reference.EventUser;
import by.raf.akunamatata.model.db.wrappers.EventCursorWrapper;

public class EventTableHelper extends DatabaseHelper {
    private static EventTableHelper instance;

    public static EventTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EventTableHelper(context);
        }
        return instance;
    }

    private EventTableHelper(Context context) {
        super(context);
    }


    @Override
    public void updateEntry(Entity entity) {
        Event event = (Event) entity;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = getContentValues(event);
        db.update(EntrySet.TABLE_NAME, cv, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                new String[]{event.getId()});
        db.close();
    }

    @Override
    synchronized public void addEntry(Entity entity) {
        Event event = (Event) entity;
        SQLiteDatabase mDatabase = null;
        try {
            ContentValues cv = getContentValues(event);
            mDatabase = getWritableDatabase();
            if (!exists(mDatabase, EntrySet.TABLE_NAME, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{entity.getId()})) {
                mDatabase.insert(EntrySet.TABLE_NAME, null, cv);
            }
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
    }

    @Override
    synchronized public void deleteEntry(Entity entity) {
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            mDatabase.delete(EntrySet.TABLE_NAME, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?", new String[]{entity.getId()});
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }

    }

    @Override
    protected <T extends Entity> ContentValues getContentValues(T entity) {
        Event event = (Event) entity;
        ContentValues cv = new ContentValues();
        cv.put(EntrySet.COLUMN_NAME_ENTRY_ID, event.getId());
        cv.put(EntrySet.COLUMN_NAME_AUTHOR, event.getAuthor());
        cv.put(EntrySet.COLUMN_NAME_PICTURE, event.getPicture());
        cv.put(EntrySet.COLUMN_NAME_TITLE, event.getTitle());
        cv.put(EntrySet.COLUMN_NAME_DESCRIPTION, event.getDescription());
        cv.put(EntrySet.COLUMN_NAME_DATE_START, event.getDateStart());
        cv.put(EntrySet.COLUMN_NAME_DATE_END, event.getDateEnd());
        return cv;
    }

    @Override
    synchronized public HashMap<String, Event> getAllEntries(String where, String[] whereArgs) {
        String MY_QUERY = "SELECT e.*, eu." + EventUser.EntrySet.COLUMN_NAME_USER_ID +
                " AS uid, eu."+EventUser.EntrySet.COLUMN_NAME_USER_HERE+" AS ubool, ec." + EventCategory.EntrySet.COLUMN_NAME_CATEGORY_ID +
                " AS cid FROM " + EventTableHelper.EntrySet.TABLE_NAME + " e" +
                " LEFT JOIN " + EventCategory.EntrySet.TABLE_NAME + " ec" +
                " ON e." + EventTableHelper.EntrySet.COLUMN_NAME_ENTRY_ID + " = ec." +
                EventCategory.EntrySet.COLUMN_NAME_EVENT_ID +
                " LEFT JOIN " + EventUser.EntrySet.TABLE_NAME + " eu" +
                " ON eu." + EventUser.EntrySet.COLUMN_NAME_EVENT_ID + " = e." +
                EntrySet.COLUMN_NAME_ENTRY_ID;
        if (where != null) {
            MY_QUERY += " " + where;
        }

        SQLiteDatabase mDatabase = null;
        Cursor cursor = null;
        HashMap<String, Event> map = new HashMap<>();
        try {
            mDatabase = getWritableDatabase();
            cursor = mDatabase.rawQuery(MY_QUERY, whereArgs);
            if (cursor.moveToFirst()) {
                EventCursorWrapper wrapper = new EventCursorWrapper(cursor);
                do {
                    Event event = wrapper.getEntry();
                    Event settedEvent = map.get(event.getId());
                    if (settedEvent != null) {
                        settedEvent.getUsers().putAll(event.getUsers());
                        settedEvent.getCategoryIds().putAll(event.getCategoryIds());
                        map.put(settedEvent.getId(), settedEvent);
                    } else {
                        map.put(event.getId(), event);
                    }
                } while (cursor.moveToNext());
            }

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
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_DATE_START = "date_start";
        public static final String COLUMN_NAME_DATE_END = "date_end";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

}
