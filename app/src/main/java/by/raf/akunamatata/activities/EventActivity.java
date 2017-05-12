package by.raf.akunamatata.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import by.raf.akunamatata.R;
import by.raf.akunamatata.fragments.AkunaMatataFragment;
import by.raf.akunamatata.fragments.EventFragment;
import by.raf.akunamatata.fragments.ViewPagerFragment;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;

public class EventActivity extends SingleFragmentActivity {

    public static final String POSITION = "POSITION";
    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(POSITION, position);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return ViewPagerFragment.newInstance(getIntent().getExtras().getInt(POSITION, 0));
    }


}
