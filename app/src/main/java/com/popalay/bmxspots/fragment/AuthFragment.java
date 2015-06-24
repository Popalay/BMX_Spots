package com.popalay.bmxspots.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.popalay.bmxspots.MainActivity;
import com.popalay.bmxspots.R;
import com.vk.sdk.VKScope;

import java.util.List;

public class AuthFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener,
        OnLoginCompleteListener, OnRequestSocialPersonCompleteListener {

    public interface SocialFragmentListener {
        void loggedInSocialNetwork(int networkID);
    }

    public static final String TAG = "AuthFragment";

    private SocialFragmentListener socialFragmentListener;

    public AuthFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            socialFragmentListener = (SocialFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auth, container, false);
        // init buttons and set Listener
        Button vk = (Button) rootView.findViewById(R.id.vk);
        vk.setOnClickListener(loginClick);

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        String[] vkScope = new String[]{
                VKScope.FRIENDS,
                VKScope.WALL,
                VKScope.PHOTOS,
                VKScope.NOHTTPS,
                VKScope.STATUS,
        };

        //Use manager to manage SocialNetworks
        MainActivity.mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(MainActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (MainActivity.mSocialNetworkManager == null) {
            MainActivity.mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager VkSocialNetwork
            VkSocialNetwork vkNetwork = new VkSocialNetwork(this, VK_KEY, vkScope);
            MainActivity.mSocialNetworkManager.addSocialNetwork(vkNetwork);

            //Initiate every network from mSocialNetworkManager
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(MainActivity.mSocialNetworkManager, MainActivity.SOCIAL_NETWORK_TAG)
                    .commit();
            MainActivity.mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if (!MainActivity.mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = MainActivity.mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    private void initSocialNetwork(SocialNetwork socialNetwork) {
        if (socialNetwork.isConnected()) {
            switch (socialNetwork.getID()) {
                case VkSocialNetwork.ID:
                    break;
            }
            logged(socialNetwork.getID());
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : MainActivity.mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("click", "loginClick()");
            int networkId = 0;
            switch (view.getId()) {
                case R.id.vk:
                    networkId = VkSocialNetwork.ID;
                    break;
            }
            SocialNetwork socialNetwork = MainActivity.mSocialNetworkManager.getSocialNetwork(networkId);
            if (!socialNetwork.isConnected()) {
                if (networkId != 0) {
                    socialNetwork.requestLogin();
                    MainActivity.showProgress("Loading vk person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "You are in app", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        MainActivity.hideProgress();
        Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_LONG).show();
        MainActivity.showProgress("Register in app");
        SocialNetwork socialNetwork = MainActivity.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        MainActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        MainActivity.hideProgress();
        registerOrLoginUser(i, socialPerson.id, socialPerson.name, socialPerson.avatarURL);

    }

    private void registerOrLoginUser(final int networkID, final String id, final String name, String avatar) {

        ParseUser newUser = new ParseUser();
        newUser.setUsername(name);
        newUser.setPassword(id);

        // other fields can be set just like with ParseObject
        newUser.put("avatar", avatar);
        if (newUser.isNew()) {
            newUser.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        signIn(networkID, name, id);
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        Log.d(TAG, e.getMessage() );
                    }
                }
            });
        } else {
            signIn(networkID, name, id);
        }
    }

    private void signIn(final int networkID, String name, String password) {
        ParseUser.logInInBackground(name, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    logged(networkID);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.d(TAG, e.getMessage() );
                }
            }
        });
    }

    private void logged(int networkID) {
        Log.d("networkID", "inLogin: " + networkID);
        socialFragmentListener.loggedInSocialNetwork(networkID);
    }
}
