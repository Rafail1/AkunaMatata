package by.raf.akunamatata.model.db.wrappers;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.HashMap;

import by.raf.akunamatata.model.Event;
import by.raf.akunamatata.model.db.EventTableHelper;
import by.raf.akunamatata.model.db.reference.EventCategory;
import by.raf.akunamatata.model.db.reference.EventUser;

public class EventCursorWrapper extends CursorWrapper {
    public EventCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Event getEntry() {

        String id = getString(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_ENTRY_ID));
        String title = getString(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_TITLE));
        String author = getString(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_AUTHOR));
        String description = getString(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_DESCRIPTION));
        String picture = getString(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_PICTURE));
        long date_end= getLong(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_DATE_END));
        long  date_start = getLong(getColumnIndex(EventTableHelper.EntrySet.COLUMN_NAME_DATE_START));
        String userId = getString(getColumnIndex("uid"));
        boolean user_bool = getInt(getColumnIndex("ubool")) > 0;
        String categoryId = getString(getColumnIndex("cid"));
        HashMap<String, Boolean> users = new HashMap<>();
        HashMap<String, Boolean> categories = new HashMap<>();
        users.put(userId, user_bool);
        categories.put(categoryId, true);
        Event event = new Event();
        event.setId(id);
        event.setTitle(title);
        event.setAuthor(author);
        event.setDescription(description);
        event.setPicture(picture);
        event.setDateStart(date_start);
        event.setDateEnd(date_end);
        return event;
    }

    HashMap<String, Boolean> getEventCategories(String event_id, Context context) {
        return EventCategory.getInstance(context).getEntries(" WHERE " + EventCategory.EntrySet.COLUMN_NAME_EVENT_ID
                + " = ?", new String[]{event_id});
    }
    HashMap<String, Boolean> getEventUsers(String eventId, Context context) {
        return EventUser.getInstance(context).getEntries(" WHERE " + EventUser.EntrySet.COLUMN_NAME_EVENT_ID + " = ?",
                new String[]{eventId});
    }
}