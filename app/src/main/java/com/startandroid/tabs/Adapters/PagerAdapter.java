package com.startandroid.tabs.Adapters;

/**
 * Created by Денис on 18.06.2016.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.startandroid.tabs.Fragments.MapFragment;
import com.startandroid.tabs.Fragments.PlaceListFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MapFragment map = new MapFragment();
                return map;
            case 1:
                PlaceListFragment placeListFragment = new PlaceListFragment();
                return placeListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}