package com.popalay.bmxspots;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "OTgGHGUpjgTFTxAlxzwg85bdJ8Fx3cEGvRNKwNQt", "kJ0iOKH94wvE4nHut0rF5WFcZOOX8H1uQwFkfkvW");
    }
}
