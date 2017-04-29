package by.raf.akunamatata.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

import by.raf.akunamatata.model.Entity;
import by.raf.akunamatata.model.db.reference.EventCategory;
import by.raf.akunamatata.model.db.reference.EventUser;


public abstract class DatabaseHelper extends SQLiteOpenHelper {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_DELETE = 1;

    public static final int ACTION_UPDATE = 2;
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String REAL_TYPE = " REAL";
    public static final String COMMA_SEP = ",";
    protected static final String UNIQUE = " not null unique";
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "AkunaMatata.db";
    private static final String SQL_CREATE_USER = "CREATE TABLE " + UserTableHelper.EntrySet.TABLE_NAME + " (" +
    UserTableHelper.EntrySet._ID + " INTEGER PRIMARY KEY," +
    UserTableHelper.EntrySet.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + UNIQUE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_SEX + INTEGER_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_BIRTHDAY + INTEGER_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
    UserTableHelper.EntrySet.COLUMN_NAME_LON + REAL_TYPE +
            " )";
    private static final String SQL_CREATE_CATEGORY = "CREATE TABLE " + CategoryHelper.EntrySet.TABLE_NAME + " (" +
            CategoryHelper.EntrySet._ID + " INTEGER PRIMARY KEY," +
            CategoryHelper.EntrySet.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + UNIQUE + COMMA_SEP +
            CategoryHelper.EntrySet.COLUMN_NAME_NAME + TEXT_TYPE +
            " )";
    private static final String SQL_CREATE_EVENT = "CREATE TABLE " + EventTableHelper.EntrySet.TABLE_NAME + " (" +
            EventTableHelper.EntrySet._ID + " INTEGER PRIMARY KEY," +
            EventTableHelper.EntrySet.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + UNIQUE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_DATE_START + INTEGER_TYPE + COMMA_SEP +
            EventTableHelper.EntrySet.COLUMN_NAME_DATE_END + INTEGER_TYPE +
            " )";
    private static final String SQL_CREATE_EVENT_USER = "CREATE TABLE " + EventUser.EntrySet.TABLE_NAME + " (" +
            EventUser.EntrySet._ID + " INTEGER PRIMARY KEY," +
            EventUser.EntrySet.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
            EventUser.EntrySet.COLUMN_NAME_EVENT_ID + TEXT_TYPE + COMMA_SEP +
            EventUser.EntrySet.COLUMN_NAME_USER_HERE + INTEGER_TYPE +
            " )";
    private static final String SQL_CREATE_EVENT_CATEGORY = "CREATE TABLE " + EventCategory.EntrySet.TABLE_NAME + " (" +
            EventCategory.EntrySet._ID + " INTEGER PRIMARY KEY," +
            EventCategory.EntrySet.COLUMN_NAME_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +
            EventCategory.EntrySet.COLUMN_NAME_EVENT_ID + TEXT_TYPE +
            " )";
    protected DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void update(int action, Entity entity) {
        switch (action) {
            case ACTION_ADD:
                addEntry(entity);
                break;
            case ACTION_UPDATE:
                updateEntry(entity);
                break;
            case ACTION_DELETE:
                deleteEntry(entity);
                break;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY);
        sqLiteDatabase.execSQL(SQL_CREATE_USER);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_CATEGORY);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_USER);
    }

    protected boolean exists(SQLiteDatabase db, String table, String where, String[] whereArgs) {
        Cursor cursor = db.query(table, null, where, whereArgs, null, null, null);
        boolean res = cursor.getCount() > 0;
        cursor.close();
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryHelper.EntrySet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserTableHelper.EntrySet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventTableHelper.EntrySet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventCategory.EntrySet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventUser.EntrySet.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public abstract void deleteEntry(Entity entity);

    public abstract void updateEntry(Entity entity);

    public abstract void addEntry(Entity entity);

    protected <T extends Entity> ContentValues getContentValues(T entity) throws Exception {
        throw new Exception("Don\'t use it");
    }

    protected HashMap<String, ? extends Entity> getAllEntries(String where, String[] whereArgs) throws Exception {
        throw new Exception("Don\'t use it");
    }
}
