package by.raf.akunamatata.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class DatabaseHelper extends SQLiteOpenHelper {
    static final String TEXT_TYPE = " TEXT";
    static final String INTEGER_TYPE = " INTEGER";
    static final String COMMA_SEP = ",";
    protected static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "AkunaMatata.db";

    protected DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateSQL());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getUpgradeSQL());
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    protected abstract String getCreateSQL();

    protected abstract String getUpgradeSQL();
}
