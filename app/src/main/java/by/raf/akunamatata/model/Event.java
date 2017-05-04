package by.raf.akunamatata.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by raf on 4/21/17.
 */

public class Event extends Entity {
    public static int OBSERVER_ID = 1;
    public static final int CHANGED = 0;
    public static final int ADDED = 1;
    public static final int REMOVED = 2;
    private String mAuthor = "";
    private String mTitle = "";
    private HashMap<String, Integer> mUsers;
    private String mPicture  = "";
    private String mDescription  = "";
    private HashMap<String, Boolean> mCategoryIds;
    private String mId;
    private long mLat;
    private long mLon;
    private long mDateStart;
    private long mDateEnd;
    private String mAddress  = "";

    public Event() {
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPicture() {
        return mPicture;
    }

    public String getDescription() {
        return mDescription;
    }


    public void setPicture(String picture) {
        mPicture = picture;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public HashMap<String, Integer> getUsers() {
        if (mUsers == null) {
            mUsers = new HashMap<>();
        }
        return mUsers;
    }

    public void setUsers(HashMap<String, Integer> users) {
        mUsers = users;
    }

    public int getCount() {
        if (mUsers == null) {
            return 0;
        }
        return mUsers.size();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public long getDateStart() {
        return mDateStart;
    }

    public void setDateStart(long dateStart) {
        mDateStart = dateStart;
    }

    public long getDateEnd() {
        return mDateEnd;
    }

    public void setDateEnd(long dateEnd) {
        mDateEnd = dateEnd;
    }

    public HashMap<String, Boolean> getCategoryIds() {
        return mCategoryIds;
    }

    public void setCategoryIds(HashMap<String, Boolean> categoryIds) {
        mCategoryIds = categoryIds;
    }

    public String setAddress() {
        return mAddress;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public long getLat() {
        return mLat;
    }

    public void setLat(long lat) {
        mLat = lat;
    }

    public long getLon() {
        return mLon;
    }

    public void setLon(long lon) {
        mLon = lon;
    }

    public float[] mygetStat() {
        HashMap<String, Integer> users = getUsers();
        Iterator it = users.entrySet().iterator();
        float mansRaw = 0;
        float girlsRaw = 0;
        float fullRaw = 0;
        float girls = 0;
        float mans = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int mask = (int) pair.getValue();
            if ((mask & User.GENDER_MAN) > 0) {
                mansRaw++;
            } else if ((mask & User.GENDER_WOMAN) > 0) {
                girlsRaw++;
            }
            fullRaw++;
        }


        if(fullRaw == 0) {
            girls = 0;
            mans = 0;
        } else {
            girls = girlsRaw / fullRaw * 100;
            mans = mansRaw / fullRaw * 100;
        }
        return new float[]{mans, girls, fullRaw};
    }
}
