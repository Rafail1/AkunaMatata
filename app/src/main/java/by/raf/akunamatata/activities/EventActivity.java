package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.google.firebase.database.ChildEventListener;

import by.raf.akunamatata.fragments.ViewPagerFragment;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;

public class EventActivity extends SingleFragmentActivity implements ViewPagerFragment.Callbacks {

    public static final String POSITION = "POSITION";
    private ChildEventListener listener;
    private Event mEvent;

    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(POSITION, position);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getIntent().getIntExtra(POSITION, 0);
        mEvent = DataProvider.getInstance().getEventList().get(position);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mEvent.getTitle());
        }
    }

    @Override
    public Fragment createFragment() {
        return ViewPagerFragment.newInstance(getIntent().getExtras().getInt(POSITION, 0));
    }
    @Override
    protected void onStart() {
        super.onStart();
        DataProvider provider = DataProvider.getInstance();
        listener = provider.getListener(Event.class, provider.sEvents);
        provider.myRefEvents.orderByKey().equalTo(mEvent.getId()).addChildEventListener(listener);
    }
    @Override
    protected void onStop() {
        DataProvider provider = DataProvider.getInstance();
        provider.myRefEvents.removeEventListener(listener);
        super.onStop();
    }

    @Override
    public void onPageSelected(int position) {
        mEvent = DataProvider.getInstance().getEventList().get(position);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mEvent.getTitle());
        }
    }
}
