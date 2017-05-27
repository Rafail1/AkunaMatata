package by.raf.akunamatata.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by raf on 5/27/17.
 */

public class UserFragment extends Fragment {
    public static UserFragment newInstance() {
        Bundle args = new Bundle();
        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
