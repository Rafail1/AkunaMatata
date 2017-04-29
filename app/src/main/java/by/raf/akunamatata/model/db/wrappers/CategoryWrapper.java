package by.raf.akunamatata.model.db.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import by.raf.akunamatata.model.Category;
import by.raf.akunamatata.model.db.CategoryHelper;

/**
 * Created by raf on 4/28/17.
 */

public class CategoryWrapper extends CursorWrapper {

    public CategoryWrapper(Cursor cursor) {
        super(cursor);
    }

    public Category getEntry() {
        String id = getString(getColumnIndex(CategoryHelper.EntrySet.COLUMN_NAME_ENTRY_ID));
        String name = getString(getColumnIndex(CategoryHelper.EntrySet.COLUMN_NAME_NAME));
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}