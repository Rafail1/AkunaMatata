package by.raf.akunamatata.model;

import java.util.HashMap;

/**
 * Created by raf on 4/21/17.
 */

public class Event extends Entity {
    public static int OBSERVER_ID = 1;
    private String mAuthor = "";
    private String mTitle = "";
    private HashMap<String, Integer> mUsers;
    private String mPicture  = "";
    private String mDescription  = "";
    private HashMap<String, Boolean> mCategoryIds;
    private String mId;
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
}
