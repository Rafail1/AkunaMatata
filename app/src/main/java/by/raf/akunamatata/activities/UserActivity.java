package by.raf.akunamatata.activities;

import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.UserFragment;

/**
 * Created by raf on 5/27/17.
 */

public class UserActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return UserFragment.newInstance();
    }
}
