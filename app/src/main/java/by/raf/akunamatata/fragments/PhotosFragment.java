package by.raf.akunamatata.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.raf.akunamatata.R;

public class PhotosFragment extends Fragment {
    public static final String ARG_EVENT_ID = "ARG_EVENT_ID";

    public static PhotosFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_photos, container, false);
        return v;
    }
}
