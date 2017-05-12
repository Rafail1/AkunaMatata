package by.raf.akunamatata.model;

/**
 * Created by raf on 4/26/17.
 *
 * ▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬
 K O D - E T O G O - C H E L O V E K A - P R E K R A S E N
 ▬▬▬▬▬▬▬▬▬▬ஜ۩۞۩ஜ▬▬▬▬▬▬▬▬▬▬
 */

public class Category extends Entity {
    public static final int OBSERVER_ID = 2;
    private String mId;
    private String mName;
    private String mIcon;
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

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }
}
