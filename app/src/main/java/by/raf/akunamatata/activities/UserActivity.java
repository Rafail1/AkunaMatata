package by.raf.akunamatata.activities;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import by.raf.akunamatata.fragments.UserFragment;

public class UserActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return UserFragment.newInstance();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, UserActivity.class);
    }
}
