package by.raf.akunamatata.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.raf.akunamatata.R;
import by.raf.akunamatata.activities.EventActivity;
import by.raf.akunamatata.model.DataProvider;

/**
 * Created by raf on 5/11/17.
 */

public class ViewPagerFragment extends Fragment {
    private static final String ARG_POS = "ARG_POS";
    private ViewPager mViewPager;
    public static ViewPagerFragment newInstance(int pos) {
        Bundle args = new Bundle();
        args.putInt(ARG_POS, pos);
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_pager, container, false);
        return v;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return EventFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return DataProvider.getInstance().mEventList.size();
            }
        });
        mViewPager.setCurrentItem(getArguments().getInt(ARG_POS));
    }
}
