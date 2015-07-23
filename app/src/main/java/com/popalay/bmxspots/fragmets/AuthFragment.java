package com.popalay.bmxspots.fragmets;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.vk.VkSocialNetwork;
import com.parse.ParseUser;
import com.popalay.bmxspots.R;
import com.popalay.bmxspots.activities.MainActivity;
import com.vk.sdk.VKScope;

import java.util.List;

public class AuthFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener,
        OnLoginCompleteListener, OnRequestSocialPersonCompleteListener {

    public interface AuthFragmentListener {
        void loggedInSocialNetwork(int networkID);
    }

    public static final String TAG = "AuthFragment";

    private AuthFragmentListener authFragmentListener;
    private View rootView;

    public AuthFragment() {
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            authFragmentListener = (AuthFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_auth, container, false);
        // init buttons and set Listener
        Button vk = (Button) rootView.findViewById(R.id.vk);
        vk.setOnClickListener(loginClick);

        //Get Keys for initiate SocialNetworks
        String VK_KEY = getActivity().getString(R.string.vk_app_id);

        String[] vkScope = new String[]{
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

    @Override public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : MainActivity.mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    private View.OnClickListener loginClick = view -> {
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
                Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.coord), "Wrong networkID", Snackbar.LENGTH_SHORT);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.coord), "You are in app", Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
            snackbar.show();
        }
    };

    @Override public void onLoginSuccess(int networkId) {
        MainActivity.hideProgress();
        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.coord), "Login success", Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
        MainActivity.showProgress("Register in app");
        SocialNetwork socialNetwork = MainActivity.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);
        socialNetwork.requestCurrentPerson();
    }

    @Override public void onError(int networkId, String requestID, String errorMessage, Object data) {
        MainActivity.hideProgress();
        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.coord), "ERROR: " + errorMessage, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        MainActivity.hideProgress();
        registerOrLoginUser(i, socialPerson.id, socialPerson.name, socialPerson.avatarURL, socialPerson.profileURL);

    }

    private void registerOrLoginUser(final int networkID, final String id, final String name, final String avatar, final String link) {
        ParseUser newUser = new ParseUser();
        newUser.setUsername(name);
        newUser.setPassword(id);

        // other fields can be set just like with ParseObject
        newUser.put("avatar", avatar);
        newUser.put("link", link);
        if (newUser.isNew()) {
            newUser.signUpInBackground(e -> {
                if (e == null) {
                    signIn(networkID, name, id);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d(TAG, e.getMessage() );
                }
            });
        } else {
            signIn(networkID, name, id);
        }
    }

    private void signIn(final int networkID, final String name, final String password) {
        ParseUser.logInInBackground(name, password, (user, e) -> {
            if (user != null) {
                logged(networkID);
            } else {
                // Sign up failed. Look at the ParseException to see what happened.
                Log.d(TAG, e.getMessage() );
            }
        });
    }

    private void logged(int networkID) {
        Log.d("networkID", "inLogin: " + networkID);
        authFragmentListener.loggedInSocialNetwork(networkID);
    }
}
