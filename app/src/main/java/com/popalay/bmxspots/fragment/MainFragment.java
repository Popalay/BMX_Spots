package com.popalay.bmxspots.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.gorbin.asne.core.SocialNetwork;
import com.parse.ParseUser;
import com.popalay.bmxspots.MainActivity;
import com.popalay.bmxspots.R;

public class MainFragment extends Fragment {

    public interface MainFragmentListener {
        void notLoggedInSocialNetwork();
    }

    public static final String TAG = "MainFragment";

    private static final String NETWORK_ID = "NETWORK_ID";

    private SocialNetwork socialNetwork;
    private int networkId;

    private Toolbar toolbar;
    private View rootView;
    private Button logout;
    private MainFragmentListener mainFragmentListener;


    public MainFragment() {
    }

    public static MainFragment newInstance(int id) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainFragmentListener = (MainFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        logout = (Button) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(logoutClick);

        networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;
        socialNetwork = MainActivity.mSocialNetworkManager.getSocialNetwork(networkId);

        initToolbar();
        return rootView;
    }

    private void initToolbar() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private View.OnClickListener logoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("click", "logoutClick()");
            logout();
        }
    };

    private void logout() {
        socialNetwork.logout();
        ParseUser.logOut();
        mainFragmentListener.notLoggedInSocialNetwork();
    }


}
