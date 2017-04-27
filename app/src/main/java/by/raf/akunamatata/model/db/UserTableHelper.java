package by.raf.akunamatata.model.db;

import android.content.Context;
import android.provider.BaseColumns;

public class UserTableHelper extends DatabaseHelper {
    private static String SQL_CREATE_ENTRIES =  "CREATE TABLE " + UserTableHelper.UserEntry.TABLE_NAME + " (" +
            UserTableHelper.UserEntry._ID + " INTEGER PRIMARY KEY," +
            UserTableHelper.UserEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_SEX + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_BIRTHDAY + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_DATE_CREATED + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
            UserTableHelper.UserEntry.COLUMN_NAME_LON + TEXT_TYPE + COMMA_SEP +
            " )";
    private static String SQL_DELETE_ENTRIES =  "DROP TABLE IF EXISTS " + UserTableHelper.UserEntry.TABLE_NAME;

    protected UserTableHelper(Context context) {
        super(context);
    }

    @Override
    protected String getCreateSQL() {
        return SQL_CREATE_ENTRIES;
    }

    @Override
    protected String getUpgradeSQL() {
        return SQL_DELETE_ENTRIES;
    }

    static abstract class UserEntry implements BaseColumns {
        static final String TABLE_NAME = "user";
        static final String COLUMN_NAME_ENTRY_ID = "id";
        static final String COLUMN_NAME_SEX = "sex";
        static final String COLUMN_NAME_PICTURE = "picture";
        static final String COLUMN_NAME_BIRTHDAY = "birthday";
        static final String COLUMN_NAME_DATE_CREATED = "date_created";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_LAT = "lat";
        static final String COLUMN_NAME_LON = "lon";
    }
}
