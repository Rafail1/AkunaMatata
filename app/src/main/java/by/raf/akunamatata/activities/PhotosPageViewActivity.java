package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import by.raf.akunamatata.R;
import by.raf.akunamatata.fragments.EventFragment;
import by.raf.akunamatata.model.DataProvider;

/**
 * Created by raf on 5/8/17.
 */

public class PhotosPageViewActivity extends AppCompatActivity {
    public static final String PHOTO_ID = "PHOTO_ID";
    public static Intent newIntent(Context packageContext, String photoId) {
        Intent intent = new Intent(packageContext, PhotosPageViewActivity.class);
        intent.putExtra(PHOTO_ID, photoId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_activity);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return EventFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return 0;
            }
        });
        viewPager.setCurrentItem(getIntent().getExtras().getInt(EventActivity.POSITION));
    }
}
