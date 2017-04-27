package by.raf.akunamatata.activities;

import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.EventFragment;

public class EventActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return EventFragment.newInstance();
    }
}
