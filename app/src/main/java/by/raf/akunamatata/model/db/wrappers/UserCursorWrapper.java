package by.raf.akunamatata.model.db.wrappers;

import android.database.Cursor;
import android.database.CursorWrapper;

import by.raf.akunamatata.model.User;
import by.raf.akunamatata.model.db.UserTableHelper;

/**
 * Created by raf on 4/28/17.
 */

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getEntry() {
        String id = getString(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_ENTRY_ID));
        String name = getString(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_NAME));
        String last_name = getString(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_LAST_NAME));
        int sex = getInt(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_SEX));
        String picture = getString(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_PICTURE));
        long birthday = getLong(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_BIRTHDAY));
        String status = getString(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_STATUS));
        double lat = getDouble(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_LAT));
        double lon = getDouble(getColumnIndex(UserTableHelper.EntrySet.COLUMN_NAME_LON));

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setLastName(last_name);
        user.setSex(sex);
        user.setPicture(picture);
        user.setBirthDay(birthday);
        user.setStatus(status);
        user.setLat(lat);
        user.setLon(lon);
        return user;
    }
}