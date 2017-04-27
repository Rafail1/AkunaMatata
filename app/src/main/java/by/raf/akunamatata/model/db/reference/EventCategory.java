package by.raf.akunamatata.model.db.reference;

import android.content.Context;

import by.raf.akunamatata.model.db.DatabaseHelper;


public class EventCategory extends DatabaseHelper {
    EventCategory(Context context) {
        super(context);
    }

    @Override
    protected String getCreateSQL() {
        return null;
    }

    @Override
    protected String getUpgradeSQL() {
        return null;
    }
}
