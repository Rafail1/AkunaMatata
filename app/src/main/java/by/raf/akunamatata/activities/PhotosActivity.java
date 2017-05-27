package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import by.raf.akunamatata.fragments.PhotosFragment;
import by.raf.akunamatata.model.DataProvider;


public class PhotosActivity extends SingleFragmentActivity {
    public static final String PARAM_EVENT_POS = "PARAM_EVENT_POS";

    public static Intent newIntent(Context context, int eventPos) {
        Intent intent = new Intent(context, PhotosActivity.class);
        intent.putExtra(PARAM_EVENT_POS, eventPos);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int pos = getIntent().getIntExtra(PARAM_EVENT_POS, 0);
        ActionBar abar = getSupportActionBar();
        if (abar != null) {
            if(DataProvider.getInstance().getEventList().get(pos) != null) {
                abar.setTitle(DataProvider.getInstance().getEventList().get(pos).getTitle());
            }
        }
    }

    @Override
    public Fragment createFragment() {
        return PhotosFragment.newInstance(getIntent().getIntExtra(PARAM_EVENT_POS, 0));
    }


}
