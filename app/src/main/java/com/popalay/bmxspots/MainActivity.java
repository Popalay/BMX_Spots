package com.popalay.bmxspots;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.popalay.bmxspots.fragmets.AuthFragment;
import com.popalay.bmxspots.fragmets.FavoriteFragment;
import com.popalay.bmxspots.fragmets.MainFragment;
import com.popalay.bmxspots.fragmets.MapFragment;
import com.popalay.bmxspots.fragmets.MyFragment;
import com.popalay.bmxspots.fragmets.ToMapClickOnFragmentListener;
import com.popalay.bmxspots.models.Spot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements AuthFragment.AuthFragmentListener,
        ToMapClickOnFragmentListener {

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;

    private static ProgressDialog pd;
    private static Context context;

    private AuthFragment authFragment;
    private int networkID;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            initToolbar();
            initNavigationDrawer();
            authFragment = new AuthFragment();
            context = this;
            toLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_map, menu);
        ImageView refresh = (ImageView) menu.findItem(R.id.refresh).getActionView();
        if (refresh != null) {
            refresh.setImageResource(R.drawable.refresh);
            refresh.setOnClickListener(v -> {
                v.animate().rotationBy(360).setInterpolator(new DecelerateInterpolator(0.5f)).start();
                // create and use new data set
                Log.d(this.toString(), "refresh clicked");
                Stream.of(getSupportFragmentManager().getFragments()).filter(f -> f instanceof MapFragment).forEach(f -> {
                    Repo.loadAllSpots();
                    ((MapFragment) f).updateMarkers();
                });
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BMX Spots");
        setSupportActionBar(toolbar);
    }

    private void initNavigationDrawer() {
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
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
                        if (getSupportActionBar() != null)
                            getSupportActionBar().setSubtitle(getResources().getString(R.string.main_string));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new MainFragment(), MainFragment.TAG)
                                .commit();
                    }
                    return true;
                case R.id.my:
                    if (getSupportFragmentManager().findFragmentByTag(MyFragment.TAG) == null) {
                        if (getSupportActionBar() != null)
                            getSupportActionBar().setSubtitle(getResources().getString(R.string.my_string));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new MyFragment(), MyFragment.TAG)
                                .commit();
                    }
                    return true;
                case R.id.favorite:
                    if (getSupportFragmentManager().findFragmentByTag(FavoriteFragment.TAG) == null) {
                        if (getSupportActionBar() != null)
                            getSupportActionBar().setSubtitle(getResources().getString(R.string.favorite_string));
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new FavoriteFragment(), FavoriteFragment.TAG)
                                .commit();
                    }
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
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (actionBar.isShowing())
                    actionBar.hide();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, authFragment, AuthFragment.TAG)
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
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                if (!actionBar.isShowing())
                    actionBar.show();
            }
            getSupportActionBar().setSubtitle(getResources().getString(R.string.main_string));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), MainFragment.TAG)
                    .commit();
        }
        updateUserInfo();
    }

    @Override
    public void toMapClickOnFragment(Spot spot) {
        if (getSupportFragmentManager().findFragmentByTag(MainFragment.TAG) == null) {
            /*if (getSupportActionBar() != null)
                getSupportActionBar().setSubtitle(getResources().getString(R.string.main_string));
            navigationView.getMenu().getItem(0).setChecked(true);
            MainFragment mainFragment = MainFragment.newInstance(spot.getObjectId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mainFragment, MainFragment.TAG)
                    .commit();*/
        } else {
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
            mainFragment.toMapFragmentAndFindMarker(spot.getObjectId());
        }
    }

    private void updateUserInfo() {
        if (ParseUser.getCurrentUser() != null) {
            if (isConnectingToInternet())
                Repo.loadAllSpots();
            TextView username = (TextView) findViewById(R.id.username);
            username.setText(ParseUser.getCurrentUser().getUsername());
            TextView link = (TextView) findViewById(R.id.link);
            link.setText(ParseUser.getCurrentUser().get("link").toString());
            CircleImageView avatar = (CircleImageView) findViewById(R.id.avatar);
            Picasso.with(context)
                    .load(ParseUser.getCurrentUser().get("avatar").toString())
                    .placeholder(R.drawable.user_placeholder)
                    .into(avatar);
        } else {
            toLogin();
        }
    }

    private void logout() {
        if (isConnectingToInternet()) {
            mSocialNetworkManager.getSocialNetwork(networkID).logout();
            ParseUser.logOut();
            toLogin();
        }
    }

    public static boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        Log.d("MainActivity", "Network is connected");
                        return true;
                    }
        }
        return false;
    }

    public static ParseGeoPoint getCurrentLocation() {
        if (isConnectingToInternet()) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            // Get Current Location
            Location l = locationManager.getLastKnownLocation(provider);
            return new ParseGeoPoint(l.getLatitude(), l.getLongitude());
        } else {
            return new ParseGeoPoint(0, 0);
        }
    }
}
