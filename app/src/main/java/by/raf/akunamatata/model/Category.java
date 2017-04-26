package by.raf.akunamatata.model;

/**
 * Created by raf on 4/26/17.
 */

class Category extends Entity {
    private String mId;
    private String mName;
    public Category() {}
    public Category(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
