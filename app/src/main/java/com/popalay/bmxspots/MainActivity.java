package com.popalay.bmxspots;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseObject;


public class MainActivity extends Activity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initParse();
        //testParse();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu);
    }

    private void initParse() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "OTgGHGUpjgTFTxAlxzwg85bdJ8Fx3cEGvRNKwNQt", "kJ0iOKH94wvE4nHut0rF5WFcZOOX8H1uQwFkfkvW");
    }

    private void testParse() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
