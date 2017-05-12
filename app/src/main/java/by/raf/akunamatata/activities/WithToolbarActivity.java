package by.raf.akunamatata.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import by.raf.akunamatata.R;
import by.raf.akunamatata.model.Category;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;

public class WithToolbarActivity extends AppCompatActivity implements Observer, NavigationView.OnNavigationItemSelectedListener {
    public static final String MENU_CURRENT = "MENU_CURRENT";
    private static final String MENU_CURRENT_NAME = "MENU_CURRENT_NAME";
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected ArrayList<Category> mCategories;
    private SubMenu mCategoriesSubMenu;
    private MenuItem mPreviousMenuItem;
    protected SharedPreferences sp;
    private DataProvider mDataProvider;

    @LayoutRes
    protected int getResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDataProvider.deleteObserver(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDataProvider.addObserver(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResId());
        sp = getSharedPreferences(DataProvider.AKUNA_MATATA_PREFERENCES, MODE_PRIVATE);
        onCategoryChange();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDataProvider = DataProvider.getInstance();
        mDataProvider.getCategories();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mCategoriesSubMenu = mNavigationView.getMenu().addSubMenu(0, 0, 0, R.string.menu_group_categories);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sign_out) {
            item.setCheckable(false);
            if (NetworkManager.getInstance().isNetworkConnected(this)) {
                FirebaseAuth.getInstance().signOut();
                UserManager.getInstance().logout(this);
            }
            return false;
        } else {
            if (mPreviousMenuItem.equals(item)) {
                return true;
            }
            item.setCheckable(true);
            item.setChecked(true);
            if (mPreviousMenuItem != null) {
                mPreviousMenuItem.setChecked(false);
            }
            mPreviousMenuItem = item;
            String itemTitle = item.getTitle().toString();
            sp.edit().putString(MENU_CURRENT_NAME, itemTitle).apply();
            for (int i = 0; i < mCategories.size(); i++) {
                if(mCategories.get(i).getName().equals(itemTitle)) {
                    sp.edit().putString(MENU_CURRENT, mCategories.get(i).getId()).apply();
                    onCategoryChange();
                    loadEvents();
                }
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onCategoryChange() {
        DataProvider.currentCategory = sp.getString(MENU_CURRENT, null);
    }

    protected void loadEvents() {
        DataProvider.getInstance().mEventList.clear();
        Intent intent = AkunaMatataActivity.newIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Menu menu1 = mNavigationView.getMenu();
        if(mCategories != null) {
            setCategoriesMenu();
        }
        if (NetworkManager.getInstance().isNetworkConnected(this)) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                menu1.findItem(R.id.menu_sign_out).setVisible(false);
                menu1.findItem(R.id.menu_sign_in).setVisible(true);
            } else {
                menu1.findItem(R.id.menu_sign_out).setVisible(true);
                menu1.findItem(R.id.menu_sign_in).setVisible(false);
            }
        } else {
            menu1.findItem(R.id.menu_sign_in).setVisible(false);
            menu1.findItem(R.id.menu_sign_out).setVisible(false);
        }
        return true;

    }

    private void setCategoriesMenu() {
        String currentCategoryId = sp.getString(MENU_CURRENT, null);
        mCategoriesSubMenu.clear();
        for (int i = 0; i < mCategories.size(); i++) {
            if(currentCategoryId == null) {
                currentCategoryId = mCategories.get(i).getId();
            }
            MenuItem item = mCategoriesSubMenu.add(0, i, i, mCategories.get(i).getName());
            if(mCategories.get(i).getId().equals(currentCategoryId)) {
                item.setCheckable(true);
                item.setChecked(true);
                mPreviousMenuItem = item;
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        int[] args = (int[]) o;
        if (args[0] == Category.OBSERVER_ID) {
            mCategories = ((DataProvider) observable).mCategories;
            setCategoriesMenu();
        }
    }
}

