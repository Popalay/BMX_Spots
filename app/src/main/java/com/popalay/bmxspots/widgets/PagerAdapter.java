package com.popalay.bmxspots.widgets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.popalay.bmxspots.fragmets.MapFragment;
import com.popalay.bmxspots.fragmets.SurroundingFragment;

public class PagerAdapter extends SmartFragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new SurroundingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}