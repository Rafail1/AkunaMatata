package by.raf.akunamatata.model.db;

import android.content.Context;
import android.provider.BaseColumns;

/**
 * Created by raf on 4/27/17.
 */

public class EventTableHelper extends DatabaseHelper {
    private static String SQL_CREATE_ENTRIES =  "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
            EventEntry._ID + " INTEGER PRIMARY KEY," +
            EventEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_DATE_START + INTEGER_TYPE + COMMA_SEP +
            EventEntry.COLUMN_NAME_DATE_END + INTEGER_TYPE + COMMA_SEP +
            " )";

    private static String SQL_DELETE_ENTRIES =  "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    public EventTableHelper(Context context) {
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

    static abstract class EventEntry implements BaseColumns {
        static final String TABLE_NAME = "event";
        static final String COLUMN_NAME_ENTRY_ID = "id";
        static final String COLUMN_NAME_AUTHOR = "author";
        static final String COLUMN_NAME_PICTURE = "picture";
        static final String COLUMN_NAME_DATE_START = "date_start";
        static final String COLUMN_NAME_DATE_END = "date_end";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_DESCRIPTION = "description";
    }

}
