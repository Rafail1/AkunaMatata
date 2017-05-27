package by.raf.akunamatata.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import by.raf.akunamatata.R;
import by.raf.akunamatata.model.Category;
import by.raf.akunamatata.model.DataProvider;
import by.raf.akunamatata.model.managers.NetworkManager;
import by.raf.akunamatata.model.managers.UserManager;

public class WithToolbarActivity extends AppCompatActivity implements Observer, NavigationView.OnNavigationItemSelectedListener {
    public static final String MENU_CURRENT = "MENU_CURRENT";
    protected static final String MENU_CURRENT_NAME = "MENU_CURRENT_NAME";
    protected ArrayList<Category> mCategories;
    private SubMenu mCategoriesSubMenu;
    private MenuItem mPreviousMenuItem;
    protected SharedPreferences sp;
    private DataProvider mDataProvider;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.toolbar) Toolbar mToolBar;

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
        ButterKnife.bind(this);
        sp = getSharedPreferences(DataProvider.AKUNA_MATATA_PREFERENCES, MODE_PRIVATE);
        setSupportActionBar(mToolBar);

        mDataProvider = DataProvider.getInstance();
        mDataProvider.getCategories();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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
                LoginManager.getInstance().logOut();
            }
            return false;
        } else if(id == R.id.menu_sign_in) {
            Intent intent = SignInActivity.newIntent(this);
            startActivity(intent);
        } else {
            if (preventSameMenuClick() && mPreviousMenuItem != null && mPreviousMenuItem.equals(item)) {
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

    protected boolean preventSameMenuClick() {
        return false;
    }

    private void onCategoryChange() {
        DataProvider.currentCategory = sp.getString(MENU_CURRENT, null);
        ActionBar abar = getSupportActionBar();
        if(abar != null) {
            abar.setTitle(sp.getString(MENU_CURRENT_NAME, null));
        }
    }

    protected void loadEvents() {

        Intent intent = AkunaMatataActivity.newIntent(this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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

