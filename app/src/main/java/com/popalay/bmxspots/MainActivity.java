package com.popalay.bmxspots;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.gorbin.asne.core.SocialNetworkManager;
import com.parse.Parse;
import com.parse.ParseObject;
import com.popalay.bmxspots.fragment.AuthFragment;


public class MainActivity extends AppCompatActivity{

    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    public static SocialNetworkManager mSocialNetworkManager;

    public static AuthFragment authFragment;

    private static ProgressDialog pd;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            authFragment = new AuthFragment();
            context = this;
            initParse();
            //testParse();
            toLogin();
        }
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
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, authFragment)
                .commit();
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

}
