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

public class EventActivity extends AppCompatActivity {

    public static final String POSITION = "POSITION";
    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(POSITION, position);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return EventFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return DataProvider.getInstance(EventActivity.this).mEventList.size();
            }
        });
        viewPager.setCurrentItem(getIntent().getExtras().getInt(EventActivity.POSITION));
    }

}
