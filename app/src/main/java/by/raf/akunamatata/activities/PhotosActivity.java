package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.PhotosFragment;


public class PhotosActivity extends SingleFragmentActivity {
    public static final String PARAM_EVENT_POS = "PARAM_EVENT_POS";

    public static Intent newIntent(Context context, int eventPos) {
        Intent intent = new Intent(context, PhotosActivity.class);
        intent.putExtra(PARAM_EVENT_POS, eventPos);
        return intent;
    }


    @Override
    public Fragment createFragment() {
        return PhotosFragment.newInstance(getIntent().getStringExtra(PARAM_EVENT_POS));
    }
}
