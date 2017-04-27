package by.raf.akunamatata.model;

import android.location.Location;
class User extends Entity {
    private String mId;
    private int mSex;
    private long mBirthDay;
    private String mPicture;
    private String mStatus;
    private double lat;
    private double lon;

    public User() {

    }
    public User(String id, int sex, String status) {
        mId = id;
        mSex = sex;
        mStatus = status;
    }
    public User(String id, int sex, String status, long birthDay) {
        this(id, sex, status);
        mBirthDay = birthDay;
    }
    public User(String id, int sex, String status, long birthDay, String picture) {
        this(id, sex, status, birthDay);
        mPicture = picture;
    }
    public User(String id, int sex, String status, long birthDay, String picture, Location location) {
        this(id, sex, status, birthDay, picture);
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
}
