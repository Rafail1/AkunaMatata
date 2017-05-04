package by.raf.akunamatata.model;

public class Photo extends Entity {
    private String mId;
    private String mFromUser;
    private long mDateCreate;
    private String  mEventId;

    public String getFromUser() {
        return mFromUser;
    }

    public void setFromUser(String fromUser) {
        mFromUser = fromUser;
    }

    public long getDateCreate() {
        return mDateCreate;
    }

    public void setDateCreate(long dateCreate) {
        mDateCreate = dateCreate;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void setId(String id) {
        mId = id;
    }
}