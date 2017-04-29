package by.raf.akunamatata.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.HashMap;

import by.raf.akunamatata.model.Category;
import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.db.wrappers.CategoryWrapper;

public class CategoryHelper extends DatabaseHelper {
    private static CategoryHelper instance;

    public static CategoryHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CategoryHelper(context);
        }
        return instance;
    }

    private CategoryHelper(Context context) {
        super(context);

    }

    @Override
    synchronized public void deleteEntry(Entity entity) {
        Category category = (Category) entity;
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            mDatabase.delete(EntrySet.TABLE_NAME, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{category.getId()});
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }

    }

    @Override
    synchronized public void updateEntry(Entity entity) {
        ContentValues cv = getContentValues(entity);
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();
            mDatabase.update(EntrySet.TABLE_NAME, cv, EntrySet.COLUMN_NAME_ENTRY_ID + " = ?",
                    new String[]{entity.getId()});
        } finally {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
    }

    @Override
    synchronized public void addEntry(Entity entity) {
        ContentValues cv = getContentValues(entity);
        SQLiteDatabase mDatabase = null;
        try {
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
    protected <T extends Entity> ContentValues getContentValues(T entity) {
        Category category = (Category) entity;
        ContentValues cv = new ContentValues();
        cv.put(EntrySet.COLUMN_NAME_ENTRY_ID, category.getId());
        cv.put(EntrySet.COLUMN_NAME_NAME, category.getName());
        return cv;
    }

    @Override
    synchronized public HashMap<String, Category> getAllEntries(String where, String[] whereArgs) {
        HashMap<String, Category> map = new HashMap<>();
        Cursor cursor = null;
        SQLiteDatabase mDatabase = null;
        try {
            mDatabase = getWritableDatabase();

            cursor = mDatabase.query(EntrySet.TABLE_NAME, null, where, whereArgs, null, null, null);
            CategoryWrapper wrapper = new CategoryWrapper(cursor);
            if (cursor.moveToFirst()) {
                do {
                    Category category = wrapper.getEntry();
                    map.put(category.getId(), category);
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
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
    }


}
