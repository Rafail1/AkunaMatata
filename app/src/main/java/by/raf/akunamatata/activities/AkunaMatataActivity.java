package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import by.raf.akunamatata.fragments.AkunaMatataFragment;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.Event;

import static by.raf.akunamatata.model.DataProvider.currentCategory;

public class AkunaMatataActivity extends SingleFragmentActivity implements AkunaMatataFragment.Callbacks{
    public static final int RELOADED = 3;
    private ChildEventListener listener;

    @Override
    public Fragment createFragment() {
        return AkunaMatataFragment.newInstance();
    }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(sp.getString(WithToolbarActivity.MENU_CURRENT_NAME, null));
        }
    }



    @Override
    public void loadEvents() {
        final DataProvider provider = DataProvider.getInstance();

        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long seconds = today.getTimeInMillis();
        provider.myRefEvents.orderByChild("dateStart").startAt(seconds).endAt(seconds + 3600 * 24).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                provider.mEventList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event event = child.getValue(Event.class);
                    if(currentCategory != null && !event.getCategoryIds().containsKey(currentCategory)) {
                        continue;
                    }
                    provider.mEventList.add(event);
                }
                provider.notifyObservers(Event.OBSERVER_ID, RELOADED, -1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected boolean preventSameMenuClick() {
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DataProvider provider = DataProvider.getInstance();
        listener = provider.getListener(Event.class, provider.sEvents);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        long seconds = today.getTimeInMillis();
        provider.myRefEvents.orderByChild("dateStart").startAt(seconds).endAt(seconds + 3600 * 1000 * 24)
                .addChildEventListener(listener);
    }
    @Override
    protected void onStop() {
        DataProvider provider = DataProvider.getInstance();
        provider.myRefEvents.removeEventListener(listener);
        super.onStop();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AkunaMatataActivity.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    public void onEventSelected(int eventPosition) {
        startActivity(EventActivity.newIntent(this, eventPosition));
    }
}
