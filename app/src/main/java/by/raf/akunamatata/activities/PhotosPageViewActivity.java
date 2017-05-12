package by.raf.akunamatata.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import by.raf.akunamatata.R;
import by.raf.akunamatata.fragments.OnePhotoFragment;
import by.raf.akunamatata.model.Photo;

public class PhotosPageViewActivity extends AppCompatActivity {
    public static final String PHOTO_ID = "PHOTO_ID";
    private static final String PHOTOS = "PHOTOS";
    private ArrayList<Photo> mPhotos;
    private int currentApiVersion;
    private PhotoAdapter mAdapter;
    private ViewPager viewPager;

    public static Intent newIntent(Context packageContext, String photoId, ArrayList<Photo> list) {
        Intent intent = new Intent(packageContext, PhotosPageViewActivity.class);
        intent.putExtra(PHOTO_ID, photoId);
        intent.putExtra(PHOTOS, list);
        return intent;
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private class PhotoAdapter extends FragmentPagerAdapter {

        public PhotoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return OnePhotoFragment.newInstance(mPhotos.get(position).getId());
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }
    }

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        setContentView(R.layout.view_pager);
        mPhotos = (ArrayList<Photo>) getIntent().getExtras().getSerializable(PHOTOS);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new PhotoAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        int pos = 0;
        String currentId = getIntent().getExtras().getString(PhotosPageViewActivity.PHOTO_ID);
        for (int i = 0; i < mPhotos.size(); i++) {
            if (mPhotos.get(i).getId().equals(currentId)) {
                pos = i;
                break;
            }
        }
        viewPager.setCurrentItem(pos);
    }


}
