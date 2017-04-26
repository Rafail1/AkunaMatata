package by.raf.akunamatata.activities;

import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.EventFragment;

/**
 * Created by raf on 4/21/17.
 */

public class EventActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return EventFragment.newInstance();
    }
}
