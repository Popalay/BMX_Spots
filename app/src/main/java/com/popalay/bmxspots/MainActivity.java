package com.popalay.bmxspots;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.gorbin.asne.core.SocialNetworkManager;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.popalay.bmxspots.fragment.AuthFragment;
import com.popalay.bmxspots.fragment.MainFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements AuthFragment.AuthFragmentListener{

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;

    private static ProgressDialog pd;
    private static Context context;

    private AuthFragment authFragment;
    private int networkID;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
//            initToolbar();
            initNavigationDrawer();
            authFragment = new AuthFragment();
            context = this;
            initParse();
            //testParse();
            toLogin();
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        /*toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.toolbar);*/
    }

    private void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(navigationDrawerListener);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private NavigationView.OnNavigationItemSelectedListener navigationDrawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            if(menuItem.isChecked()) menuItem.setChecked(false);
            else menuItem.setChecked(true);

            drawerLayout.closeDrawers();

            switch ((menuItem.getItemId())) {
                case R.id.home:
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "OTgGHGUpjgTFTxAlxzwg85bdJ8Fx3cEGvRNKwNQt", "kJ0iOKH94wvE4nHut0rF5WFcZOOX8H1uQwFkfkvW");
    }

    private void testParse() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "ddd");
        testObject.saveInBackground();
    }

    private void toLogin() {
        if(getSupportFragmentManager().findFragmentByTag(AuthFragment.TAG) == null) {
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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override public void loggedInSocialNetwork(int networkID) {
        this.networkID = networkID;

        if(getSupportFragmentManager().findFragmentByTag(MainFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), MainFragment.TAG)
                    .commit();
        }
        updateUserInfo();
    }

    private void updateUserInfo() {
        ParseUser currentUser  = ParseUser.getCurrentUser();
        if(currentUser != null) {
            TextView username = (TextView) findViewById(R.id.username);
            username.setText(ParseUser.getCurrentUser().getUsername());
            TextView link = (TextView) findViewById(R.id.link);
            link.setText(ParseUser.getCurrentUser().get("link").toString());
            CircleImageView avatar = (CircleImageView) findViewById(R.id.avatar);
            Picasso.with(context)
                    .load(currentUser.get("avatar").toString())
                    .placeholder(R.drawable.user_placeholder)
                    .into(avatar);
        }else {
            toLogin();
        }
    }

    private void logout() {
        mSocialNetworkManager.getSocialNetwork(networkID).logout();
        ParseUser.logOut();
        if(getSupportFragmentManager().findFragmentByTag(AuthFragment.TAG) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, authFragment, AuthFragment.TAG)
                    .commit();
        }
    }
}
