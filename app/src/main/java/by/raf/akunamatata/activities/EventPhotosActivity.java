package by.raf.akunamatata.activities;

import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.EventPhotosFragment;

public class EventPhotosActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return EventPhotosFragment.newInstance();
    }
}
