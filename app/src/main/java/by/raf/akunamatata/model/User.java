package by.raf.akunamatata.model;

import android.location.Location;

import java.io.Serializable;

public class User extends Entity implements Serializable {
    public static final int GENDER_MAN = 1;
    public static final int GENDER_WOMAN = 1<<1;
    public static final int GENDER_HZ = 1<<2;
    public static final int FREE = 1<<3;
    public static final int REGALE = 1<<4;
    public static final int DRINK = 1<<5;
    public static final int SMOKE = 1<<6;
    public static final int WANT = 1<<7;

    public static final String PREF_FREE = "FREE";
    public static final String PREF_REGALE = "REGALE";
    public static final String PREF_DRINK = "DRINK";
    public static final String PREF_SMOKE = "SMOKE";
    public static final String PREF_WANT = "WANT";
    public static final String PREF_LON = "PREF_LON";
    public static final String PREF_LAT = "PREF_LAT";
    public static final String PREF_STATUS = "PREF_STATUS";
    public static final String PREF_LAST_NAME = "LAST_NAME";
    public static final String PREF_NAME = "NAME";
    public static final String PREF_GENDER = "GENDER";
    public static final String PREF_BIRTHDAY = "BIRTHDAY";
    public static final String PREF_PHOTO = "PHOTO";
    public static String PREF_ID = "ID";

    private String mId;
    private String mName;
    private String mLastName;
    private int mSex;
    private long mBirthDay;
    private String mPicture;
    private String mStatus;
    private double lat;
    private double lon;
    private int mFree;
    private int mDrink;
    private int mWant;
    private int mSmoke;
    private int mRegale;

    public User() {}
    public User(String id, String name, String last_name, int sex, String status) {
        mId = id;
        mName = name;
        mLastName = last_name;
        mSex = sex;
        mStatus = status;
    }
    public User(String id, String name, String last_name, int sex, String status, long birthDay) {
        this(id, name, last_name, sex, status);
        mBirthDay = birthDay;
    }
    public User(String id, String name, String last_name, int sex, String status, long birthDay, String picture) {
        this(id, name, last_name, sex, status, birthDay);
        mPicture = picture;
    }
    public User(String id, String name, String last_name, int sex, String status, long birthDay, String picture, Location location) {
        this(id, name, last_name, sex, status, birthDay, picture);
        lat = location.getLatitude();
        lon = location.getLongitude();
    }



    public void setId(String id) {
        mId = id;
    }


    public String getId() {
        return mId;
    }

    public int getSex() {
        return mSex;
    }

    public void setSex(int sex) {
        mSex = sex;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getBirthDay() {
        return mBirthDay;
    }

    public void setBirthDay(long birthDay) {
        mBirthDay = birthDay;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getPicture() {
        return mPicture;
    }

    public void setPicture(String picture) {
        mPicture = picture;
    }


    public void setFree(int free) {
        mFree = free;
    }

    public void setDrink(int drink) {
        mDrink = drink;
    }

    public void setWant(int want) {
        mWant = want;
    }

    public void setSmoke(int smoke) {
        mSmoke = smoke;
    }

    public void setRegale(int regale) {
        mRegale = regale;
    }

    public Integer getMask() {
        Integer mask = 0;
        mask |= mSex;
        mask |= mFree;
        mask |= mDrink;
        mask |= mRegale;
        mask |= mSmoke;
        mask |= mWant;
        return mask;
    }
}
