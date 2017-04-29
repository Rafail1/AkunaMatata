package by.raf.akunamatata.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;

import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.User;
import by.raf.akunamatata.model.db.wrappers.UserCursorWrapper;

public class UserTableHelper extends DatabaseHelper {
    private static UserTableHelper instance;

    public static UserTableHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserTableHelper(context);
        }
        return instance;
    }

    private UserTableHelper(Context context) {
        super(context);
    }


    @Override
    synchronized public void deleteEntry(Entity entity) {
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            mDatabase.delete(EntrySet.TABLE_NAME, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{entity.getId()});
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }

    }

    @Override
    synchronized public void updateEntry(Entity entity) {
        ContentValues values = getContentValues(entity);
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            mDatabase.update(EntrySet.TABLE_NAME, values, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{entity.getId()});
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }

    }

    @Override
    synchronized public void addEntry(Entity entity) {
        ContentValues contentValues = getContentValues(entity);
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            if (!exists(mDatabase, EntrySet.TABLE_NAME, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{entity.getId()})) {
                mDatabase.insert(EntrySet.TABLE_NAME, null, contentValues);
            }
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
    }

    @Override
    protected <T extends Entity> ContentValues getContentValues(T entity) {
        User user = (User) entity;
        ContentValues cv = new ContentValues();
        cv.put(EntrySet.COLUMN_NAME_ENTRY_ID, user.getId());
        cv.put(EntrySet.COLUMN_NAME_NAME, user.getName());
        cv.put(EntrySet.COLUMN_NAME_LAST_NAME, user.getLastName());
        cv.put(EntrySet.COLUMN_NAME_BIRTHDAY, user.getBirthDay());
        cv.put(EntrySet.COLUMN_NAME_LAT, user.getLat());
        cv.put(EntrySet.COLUMN_NAME_LON, user.getLon());
        cv.put(EntrySet.COLUMN_NAME_SEX, user.getSex());
        cv.put(EntrySet.COLUMN_NAME_STATUS, user.getStatus());
        return cv;
    }

    @Override
    synchronized public HashMap<String, User> getAllEntries(String where, String[] whereArgs) {
        SQLiteDatabase mDatabase = null;
        Cursor cursor = null;
        HashMap<String, User> map = new HashMap<>();
        try {
            mDatabase = getWritableDatabase();
            cursor = mDatabase.query(EntrySet.TABLE_NAME, null, where, whereArgs,
                    null, null, null);
            UserCursorWrapper wrapper = new UserCursorWrapper(cursor);

            if (cursor.moveToFirst()) {
                do {
                    User user = wrapper.getEntry();
                    map.put(user.getId(), user);
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
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_SEX = "sex";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LON = "lon";
    }


}
