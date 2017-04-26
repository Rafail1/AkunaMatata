package by.raf.akunamatata.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by raf on 4/22/17.
 */

class User extends Entity {
    private String mId;
    private int mSex;
    private Date mBirghthDay;
    private long mCreated;
    private String mStatus;
    private double lat;
    private double lon;

    public User() {

    }
    private User(String id, int sex, String status, Date birghthDay) {
        this(id, sex, status);
        mBirghthDay = birghthDay;
    }
    public User(String id, int sex, String status, Date birghthDay, Location location) {
        this(id, sex, status, birghthDay);
        lat = location.getLatitude();
        lon = location.getLongitude();
    }
    public User(String id, int sex, String status, Location location) {
        this(id, sex, status);
        lat = location.getLatitude();
        lon = location.getLongitude();

    }
    private User(String id, int sex, String status) {
        mId = id;
        mSex = sex;
        mStatus = status;
        mCreated = new Date().getTime();
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

    public long getCreated() {
        return mCreated;
    }

    public void setCreated(long created) {
        mCreated = created;
    }

    public Date getBirghthDay() {
        return mBirghthDay;
    }

    public void setBirghthDay(Date birghthDay) {
        mBirghthDay = birghthDay;
    }
}
