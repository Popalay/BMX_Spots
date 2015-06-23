package com.popalay.bmxspots.fragment;


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

public class MainFragment  extends Fragment {

    private static final String NETWORK_ID = "NETWORK_ID";

    private SocialNetwork socialNetwork;
    private int networkId;

    private Toolbar toolbar;
    View rootView;
    Button logout;


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
            getAuth();
        }
    };

    private void getAuth() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainActivity.authFragment)
                //.addToBackStack("main")
                .commit();
    }

    private void logout() {
        socialNetwork.logout();
        ParseUser.logOut();
    }


}
