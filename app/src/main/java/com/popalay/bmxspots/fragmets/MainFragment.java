package com.popalay.bmxspots.fragmets;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.popalay.bmxspots.R;
import com.popalay.bmxspots.widgets.PagerAdapter;
import com.popalay.bmxspots.widgets.SmartFragmentStatePagerAdapter;

public class MainFragment extends Fragment {

    public static final String TAG = "MainFragment";

    private View rootView;
    private SmartFragmentStatePagerAdapter adapter;
    private ViewPager viewPager;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initTabs();
        return rootView;
    }

    private void initTabs() {
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_map)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_surrounding)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        adapter = new PagerAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void toMapFragmentAndFindMarker(String spotId) {
        viewPager.setCurrentItem(0, true);
        MapFragment mapFragment = (MapFragment) adapter.getRegisteredFragment(0);
        mapFragment.findMarker(spotId);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.saveState();
        MapFragment mapFragment = (MapFragment) adapter.getRegisteredFragment(0);
        mapFragment.saveMapState();
    }
}
