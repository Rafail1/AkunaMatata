package by.raf.akunamatata.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class EventPhotosFragment extends Fragment {

    public static EventPhotosFragment newInstance() {

        Bundle args = new Bundle();

        EventPhotosFragment fragment = new EventPhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
