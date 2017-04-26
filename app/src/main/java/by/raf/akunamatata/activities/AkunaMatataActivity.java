package by.raf.akunamatata.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;

import by.raf.akunamatata.fragments.AkunaMatataFragment;

public class AkunaMatataActivity extends SingleFragmentActivity {

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

}
