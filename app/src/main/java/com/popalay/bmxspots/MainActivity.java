package com.popalay.bmxspots;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.gorbin.asne.core.SocialNetworkManager;
import com.parse.Parse;
import com.parse.ParseUser;
import com.popalay.bmxspots.fragment.AuthFragment;
import com.popalay.bmxspots.fragment.mainfragment.MainFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements AuthFragment.AuthFragmentListener {

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;

    private static ProgressDialog pd;
    private static Context context;

    private AuthFragment authFragment;
    private int networkID;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private Repo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            initToolbar();
            initNavigationDrawer();
            authFragment = new AuthFragment();
            context = this;
            repo = new Repo();
            repo.load();
            toLogin();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BMX Spots");
        setSupportActionBar(toolbar);

    }

    private void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(navigationDrawerListener);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private NavigationView.OnNavigationItemSelectedListener navigationDrawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (menuItem.isChecked()) menuItem.setChecked(false);
            else menuItem.setChecked(true);

            drawerLayout.closeDrawers();

            switch ((menuItem.getItemId())) {
                case R.id.main:
                    if (getSupportFragmentManager().findFragmentByTag(MainFragment.TAG) == null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new MainFragment(), MainFragment.TAG)
                                .commit();
                    }
                    return true;
                case R.id.my:
                    return true;
                case R.id.favorite:
                    return true;
                case R.id.information:
                    return true;
                case R.id.logout:
                    logout();
                    return true;
                default:
                    return true;
            }
        }
    };

    private void toLogin() {
        if (getSupportFragmentManager().findFragmentByTag(AuthFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, authFragment, AuthFragment.TAG)
                    .commit();
        }
    }

    public static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    public static void hideProgress() {
        pd.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void loggedInSocialNetwork(int networkID) {
        this.networkID = networkID;

        if (getSupportFragmentManager().findFragmentByTag(MainFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), MainFragment.TAG)
                    .commit();
        }
        updateUserInfo();
    }

    private void updateUserInfo() {
        repo.setUser();
        if (repo.getUser() != null) {
            TextView username = (TextView) findViewById(R.id.username);
            username.setText(repo.getUser().getUsername());
            TextView link = (TextView) findViewById(R.id.link);
            link.setText(repo.getUser().get("link").toString());
            CircleImageView avatar = (CircleImageView) findViewById(R.id.avatar);
            Picasso.with(context)
                    .load(repo.getUser().get("avatar").toString())
                    .placeholder(R.drawable.user_placeholder)
                    .into(avatar);
        } else {
            toLogin();
        }
    }

    private void logout() {
        mSocialNetworkManager.getSocialNetwork(networkID).logout();
        ParseUser.logOut();
        if (getSupportFragmentManager().findFragmentByTag(AuthFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, authFragment, AuthFragment.TAG)
                    .commit();
        }
    }

    public Repo getRepo() {
        return repo;
    }


}
