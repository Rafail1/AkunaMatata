package by.raf.akunamatata.model.db;

import android.content.Context;

public class CategoryTableHelper extends DatabaseHelper {
    protected CategoryTableHelper(Context context) {
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
